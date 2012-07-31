@echo off
set BOOST_INCLUDE=\path_to_boost\boost\..
set PATH=path_to_mingw\bin\;%PATH%
set MINGW_INCLUDE=path_to_mingw\include\
set MINGW_LIB=path_to_mingw\lib
path_to_bjam\bjam --toolset=gcc %*
