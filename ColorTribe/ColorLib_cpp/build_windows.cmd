@echo off
set PATH=path_to_MinGW_install\bin\;%PATH%
set MINGW_INCLUDE=path_to_MinGW_install\include\
set MINGW_LIB=path_to_MinGW_install\lib\

path_to_bjam\bjam --toolset=gcc %*