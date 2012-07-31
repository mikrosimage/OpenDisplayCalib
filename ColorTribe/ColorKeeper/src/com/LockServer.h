/*
 * LockServer.h
 *
 *  Created on: Apr 26, 2012
 *      Author: mfe
 */

#ifndef LOCKSERVER_H_
#define LOCKSERVER_H_
#include <QWidget>


class QTcpServer;

class LockServer : public QWidget {
	Q_OBJECT
public:
	LockServer();
	virtual ~LockServer();
	bool isServerUp();
private:
	QTcpServer *pTcpServer;
	static const int kPort = 2353;
};

#endif /* LOCKSERVER_H_ */
