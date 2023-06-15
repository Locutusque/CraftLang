@echo off
set "batch_folder=%~dp0"
java -classpath %batch_folder%\target\classes org.locutusque.CL.CraftLang %*