/*
 * LockServer.cpp
 *
 *  Created on: Apr 26, 2012
 *      Author: mfe
 */

#include "LockServer.h"
#include <QTcpServer>
#include <QMessageBox>

LockServer::LockServer() : QWidget(), pTcpServer(NULL) {
	pTcpServer = new QTcpServer(this);
	pTcpServer->listen( QHostAddress::Any, kPort);
}

bool LockServer::isServerUp(){
	return pTcpServer->isListening();
}

LockServer::~LockServer() {
	if(pTcpServer!=NULL)
		delete pTcpServer;
}
