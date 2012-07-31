@echo off
set BOOST_INCLUDE=path_to_boost_include\
set PATH=path_to_MinGW_install\bin\;%PATH%
set MINGW_INCLUDE=path_to_MinGW_install\include\
set MINGW_LIB=path_to_MinGW_install\lib\
set COLORLIBBASE_LIB=path_to\ColorLib_cpp\
set COLORLIBBASE_SRC=path_to\ColorLib_cpp\src
path_to_bjam\bjam --toolset=gcc %*