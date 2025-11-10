@echo off

echo Cleaning...

del backup.rcp
del recipes.db
del recipes.db-journal
del resources\config.ini
del resources\credentials.txt
del MMLock
del console.log

echo Finished.