export ORGU=${TRANSPORT_PAGE_ORIGIN}
export SB=${DOCS_BRANCH_NAME}
export COM=${CI_COMMIT_MESSAGE}
export PJN=${CI_PROJECT_NAME}
mkdir gitDocs
#shellcheck disable=SC2164
cd gitDocs/
rm -rf .git/
git config --global user.name "${GRGIT_USER}"
git config --global user.email "${GRGIT_EMAIL}"
git config --global user.password "${GRGIT_PASS}"
git config --global pull.rebase true
git config --global pull.autostash true
git config --global init.defaultBranch $SB
git config --global --add merge.ff true
git init
git remote add origin $ORGU
git fetch origin $SB
git pull origin $SB
rm -rf sphinx/source/${PJN}/
cp -R ../docs/sphinx/ .
git add -A
git commit -m "$COM"
git push -f origin $SB