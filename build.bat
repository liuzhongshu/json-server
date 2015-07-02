@setlocal enableDelayedExpansion


:do_para
@if  "%1"=="help" goto do_help
@if  "%1"=="clean" goto do_clean
@if  "%1"=="run" goto do_run

:do_help
@echo.
@echo build script for cloudtopo json server
@echo usage: build [command] [para] 
@echo command can be:
@echo   clean             : clean the project
@echo   run               : run json server
@echo.
@goto end

:do_clean
call mvn clean
goto end

:do_run
@REM NUL for ctrl-c ignore in batch
call mvn compile exec:java < NUL
goto end


:end

