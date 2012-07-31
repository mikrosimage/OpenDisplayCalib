/*
 * MailSender.h
 *
 *  Created on: Jan 10, 2012
 *      Author: mfe
 */

#ifndef MAILSENDER_H_
#define MAILSENDER_H_

#include <QString>
#include <QStringList>
#include <QTcpSocket>
#include <QPointer>

class MailSender: public QObject {
Q_OBJECT

public:

	MailSender(const QString& smtpServer, const QString& sender,
			const QString& receipient);
	~MailSender();
	bool send();
	bool send(const QString& subject, const QString&body);
	QString lastError() {
		return _lastError;
	}
	QString lastCmd() {
		return _lastCmd;
	}
	QString lastResponse() {
		return _lastResponse;
	}
	QString lastMailData() {
		return _lastMailData;
	}

	void setSmtpServer(const QString &smtpServer) {
		_smtpServer = smtpServer;
	}
	void setPort(int port) {
		_port = port;
	}
	void setTimeout(int timeout) {
		_timeout = timeout;
	}
	void setCc(const QStringList &cc) {
		_cc = cc;
	}
	void setFrom(const QString &from);
	void setTo(const QStringList &to) {
		_to = to;
	}
	void setSubject(const QString &subject) {
		_subject = subject;
	}
	void setBody(const QString &body) {
		_body = body;
	}

private slots:
	void errorReceived(QAbstractSocket::SocketError socketError);

private:

	QString mailData();
	QString contentType();
	bool read(const QString &waitfor);
	bool sendCommand(const QString &cmd, const QString &waitfor);
	void error(const QString &msg);

	QString _smtpServer;
	int _port;
	int _timeout;
	QPointer<QTcpSocket> _socket;
	QString _lastError;
	QString _lastCmd;
	QString _lastResponse;
	QString _lastMailData;

	QString _from;
	QStringList _to;
	QString _subject;
	QString _body;
	QStringList _cc;
};

#endif /* MAILSENDER_H_ */
