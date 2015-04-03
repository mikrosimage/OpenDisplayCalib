######################################################################
# Automatically generated by qmake (2.01a) jeu. avr. 2 12:35:51 2015
######################################################################

TEMPLATE = lib
TARGET = ColorLib
INCLUDEPATH += ../external_libs/nv_ctrl/include
DEFINES += FTLOG
LIBS += -L../external_libs/nv_ctrl/lib -lnv-control -lX11
# NOTES:
#<os>LINUX:<linkflags>"-L/usr/X11R6/lib -lXxf86vm"		
#<os>LINUX:<linkflags>"-L/usr/lib64 -lx86" 
#<os>LINUX:<linkflags>"-L$(EXTERNAL_LIBS)/nv_ctrl/lib -lnv-control" 

CONFIG += staticlib debug

DESTDIR=lib
OBJECTS_DIR=tmp
MOC_DIR=tmp

DEPENDPATH += . \
              src/colorRamdac \
              src/device \
              src/exception \
              src/trivialLogger \
              src/calibration/correction \
              src/colorRamdac/lutloader \
              src/device/apidata \
              src/device/ediddata \
              src/device/rxdata
INCLUDEPATH += . \
               src/colorRamdac/lutloader \
               src/exception \
               src/calibration/correction \
               src/trivialLogger \
               src/device \
               src/device/ediddata \
               src/device/apidata \
               src/device/rxdata

# Input
HEADERS += src/device/DisplayDevice.h \
           src/device/DisplayDeviceScout.h \
           src/device/DisplayDeviceScoutTypeDef.h \
           src/device/LocalHost.h \
           src/exception/BinarySearchCurveException.h \
           src/exception/CannotFindLutException.h \
           src/exception/CannotGetLutException.h \
           src/exception/CannotSetLutException.h \
           src/exception/ColorRamDacException.h \
           src/trivialLogger/nullstream.hpp \
           src/trivialLogger/tri_logger.hpp \
           src/calibration/correction/BinarySearchCurve.h \
           src/calibration/correction/CurveMath.h \
           src/calibration/correction/LUT2DCorrection.h \
           src/calibration/correction/Point2D.h \
           src/colorRamdac/lutloader/AbstractLutLoader.h \
           src/colorRamdac/lutloader/GNULUTLoader.h \
           src/colorRamdac/lutloader/MacLUTLoader.h \
           src/colorRamdac/lutloader/WinLUTLoader.h \
           src/device/apidata/AbstractAPIDataLoader.h \
           src/device/apidata/APIData.h \
           src/device/apidata/WinAPIDataLoader.h \
           src/device/ediddata/AbstractEDIDLoader.h \
           src/device/ediddata/EDIDData.h \
           src/device/ediddata/LinEDIDLoader.h \
           src/device/ediddata/LRMIGetEDID.h \
           src/device/ediddata/MacOSXEDIDLoader.h \
           src/device/ediddata/NVGetEDID.h \
           src/device/ediddata/WinEDIDLoader.h \
           src/device/rxdata/AbstractRxDataLoader.h \
           src/device/rxdata/LinRxDataLoader.h \
           src/device/rxdata/WinRxDataLoader.h
SOURCES += src/device/DisplayDevice.cpp \
           src/device/DisplayDeviceScout.cpp \
           src/device/LocalHost.cpp \
           src/exception/BinarySearchCurveException.cpp \
           src/exception/CannotFindLutException.cpp \
           src/exception/CannotGetLutException.cpp \
           src/exception/CannotSetLutException.cpp \
           src/exception/ColorRamDacException.cpp \
           src/trivialLogger/tri_logger.cpp \
           src/calibration/correction/BinarySearchCurve.cpp \
           src/calibration/correction/CurveMath.cpp \
           src/calibration/correction/LUT2DCorrection.cpp \
           src/calibration/correction/Point2D.cpp \
           src/colorRamdac/lutloader/AbstractLutLoader.cpp \
           src/colorRamdac/lutloader/GNULUTLoader.cpp \
           src/colorRamdac/lutloader/MacLUTLoader.cpp \
           src/colorRamdac/lutloader/WinLUTLoader.cpp \
           src/device/apidata/AbstractAPIDataLoader.cpp \
           src/device/apidata/APIData.cpp \
           src/device/apidata/WinAPIDataLoader.cpp \
           src/device/ediddata/AbstractEDIDLoader.cpp \
           src/device/ediddata/EDIDData.cpp \
           src/device/ediddata/LinEDIDLoader.cpp \
           src/device/ediddata/LRMIGetEDID.cpp \
           src/device/ediddata/MacOSEDIDLoader.cpp \
           src/device/ediddata/NVGetEDID.cpp \
           src/device/ediddata/WinEDIDLoader.cpp \
           src/device/rxdata/AbstractRxDataLoader.cpp \
           src/device/rxdata/LinRxDataLoader.cpp \
           src/device/rxdata/WinRxDataLoader.cpp
