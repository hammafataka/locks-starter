package dev.mfataka.locks.processor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.TryTree;
import com.sun.source.util.Trees;

import dev.mfataka.locks.api.annotation.DistributedLocked;
import dev.mfataka.locks.api.annotation.ReactiveDistributedLocked;
import dev.mfataka.locks.api.annotation.ReactiveLocked;
import dev.mfataka.locks.api.annotation.SimpleLocked;
import dev.mfataka.locks.api.exception.LockAlreadyAcquiredException;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 13.04.2025 15:10
 */
@SupportedAnnotationTypes(
        {"dev.mfataka.locks.api.annotation.SimpleLocked",
                "dev.mfataka.locks.api.annotation.DistributedLocked",
                "dev.mfataka.locks.api.annotation.ReactiveDistributedLocked",
                "dev.mfataka.locks.api.annotation.ReactiveLocked"}
)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class LockedMethodSignatureProcessor extends AbstractProcessor {
    private static final Class<LockAlreadyAcquiredException> EXCEPTION = LockAlreadyAcquiredException.class;
    private Messager messager;
    private Trees trees;


    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        System.out.println("running " + getClass().getSimpleName());
        messager = processingEnv.getMessager();
        trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final var lockedMethods = roundEnv.getElementsAnnotatedWith(SimpleLocked.class);
        final var jdbcLockedMethods = roundEnv.getElementsAnnotatedWith(DistributedLocked.class);
        final var reactiveLockedMethods = roundEnv.getElementsAnnotatedWith(ReactiveLocked.class);
        final var reactiveDistributedMethods = roundEnv.getElementsAnnotatedWith(ReactiveDistributedLocked.class);

        final var elements = new HashSet<Element>();
        elements.addAll(lockedMethods);
        elements.addAll(jdbcLockedMethods);
        elements.addAll(reactiveLockedMethods);
        elements.addAll(reactiveDistributedMethods);

        elements.forEach(element -> {
            if (element.getKind() != ElementKind.METHOD) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Lock annotations can only be applied to methods.", element);
                return;
            }

            final var method = (ExecutableElement) element;
            final var thrownTypes = method.getThrownTypes();
            System.out.println("thrownTypes: " + thrownTypes);

            final var throwsRequiredException = thrownTypes.stream()
                    .map(TypeMirror::toString)
                    .anyMatch(name -> name.equals(EXCEPTION.getCanonicalName()));
            if (throwsRequiredException) {
                return;
            }

            final var path = trees.getPath(method);
            final var methodTree = (MethodTree) path.getLeaf();
            final var body = methodTree.getBody();
            final var isBodyNull = body == null;
            if (isBodyNull || body.toString().equals("{\n}")) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Method " + method.getSimpleName() + " annotated with a lock annotation must have body and declare throws " + EXCEPTION.getCanonicalName(),
                        method
                );
                return;
            }
            final var catchesLockException = body.getStatements().stream()
                    .filter(statement -> statement instanceof TryTree)
                    .map(statement -> (TryTree) statement)
                    .flatMap(tryTree -> tryTree.getCatches().stream())
                    .anyMatch(catchTree -> {
                        final var caughtType = catchTree.getParameter().getType().toString();
                        return caughtType.contains(EXCEPTION.getSimpleName()); // supports fully qualified
                    });

            if (catchesLockException) {
                return;
            }

            final var methodSignature = method.getSimpleName() + "(" +
                    method.getParameters().stream().map(p -> p.asType().toString()).collect(Collectors.joining(", ")) + ")";

            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Method " + methodSignature + " annotated with a lock annotation must either declare throws " + EXCEPTION.getCanonicalName() + " or or catch in try-catch",
                    method
            );
        });

        return true;
    }
}
