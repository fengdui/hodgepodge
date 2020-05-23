#订正git提交历史
git filter-branch -f --env-filter "
GIT_AUTHOR_NAME='fengdui';
GIT_AUTHOR_EMAIL='614173971@qq.com';
GIT_COMMITTER_NAME='fengdui';
GIT_COMMITTER_EMAIL='614173971@qq.com'
" HEAD
