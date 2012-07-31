/*
 * CKSocketClient.h
 *
 *  Created on: 17 mars 2009
 *      Author: mfe
 */

#ifndef CKSOCKETCLIENT_H_
#define CKSOCKETCLIENT_H_

#include <QWidget>
#include <QString>
#include <QAbstractSocket>

class QTcpSocket;
class QString;
class CKSocketClient: public QWidget {
	Q_OBJECT
public:
	CKSocketClient();
	virtual ~CKSocketClient();

	void startCom(const QString &serverAddress, unsigned int osScreenIndex);
	bool isConnected() const;
	void writeSocket(const QString &message);
	void playMessage(const QString &message);

private:
	QTcpSocket *_tcpSocket;
	QString _serverAddress;
	quint16 _blockSize;
	unsigned int _currentScreen;

private slots:
	void readSocket();
	void displayError(QAbstractSocket::SocketError socketError);

signals:
void displayLumContPatt(float,unsigned int, bool);
void displayPatch(unsigned int, float r, float g, float b, bool halo);
void displayFullScreenRec(unsigned int, float r, float g, float b);

};

#endif /* CKSOCKETCLIENT_H_ */
