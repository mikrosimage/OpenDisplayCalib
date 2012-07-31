@echo off
set BOOST_INCLUDE=T:\Workspace_HD3D\boost-1.35.0\fetch\boost\..
set PATH=T:\Workspace_HD3D\mingw-1.5.4\fetch\bin\;%PATH%
set MINGW_INCLUDE=T:\Workspace_HD3D\mingw-1.5.4\fetch\include\
set MINGW_LIB=T:\Workspace_HD3D\mingw-1.5.4\fetch\lib\
T:\Workspace_HD3D\boost-1.35.0\build\..\bjam --toolset=gcc %*