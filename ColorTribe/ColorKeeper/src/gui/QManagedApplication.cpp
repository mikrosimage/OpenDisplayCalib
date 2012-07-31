/*
 * QManagedApplication.cpp
 *
 *  Created on: 22 dec. 2011
 *      Author: mfe
 */

#include "QManagedApplication.h"

#include "../core/ColorKeeperModel.h"
#include <QSessionManager>

#ifdef __WIN32__
#include <Windows.h>
#endif
#include <iostream>
//using namespace std;

QManagedApplication::QManagedApplication(int &argc, char **argv) :
	QApplication(argc, argv) {

}

int QManagedApplication::exec() {
	return QApplication::exec();
//#ifdef __WIN32__
////	BOOL bRet;
////	MSG msg;
////	memset(&msg, 0, sizeof(msg));
////
////	while ((bRet = GetMessage(&msg, NULL, 0, 0)) != 0) {
////		if (msg.message == WM_QUIT || msg.message == WM_DESTROY) {
////			ColorKeeperModel::deleteLockFile();
////			return 0;
////		} else {
////			TranslateMessage(&msg);
////			DispatchMessage(&msg);
////		}
////
////	}
////	return 0;
//#endif
}




void QManagedApplication::commitData(QSessionManager & manager) {
	QApplication::commitData(manager);
	ColorKeeperModel::deleteLockFile();
	ColorKeeperModel::logMessage("commitData");
	manager.release();
}


