/*
 * MailSender.cpp
 *
 *  Created on: Jan 10, 2012
 *      Author: mfe
 */

#include "MailSender.h"
#include "SmtpServer.h"

//mailsender.cpp
#include <QString>

MailSender::MailSender(const QString& smtpServer, const QString& sender,
		const QString& receipient) {
	setSmtpServer(smtpServer);
	setPort(25);
	setTimeout(30000);
	setFrom(sender);
	QStringList recipients;
	recipients << receipient;
	setTo(recipients);
}

MailSender::~MailSender() {
	if (_socket) {
		delete _socket;
	}
}

void MailSender::setFrom(const QString &from) {
	_from = from;

}



QString MailSender::mailData() {
	QString data;
	////////////////
	//Minimum Data (Less data = less chances to be reported as spam by postini)
	///////////////
	data.append("From: <" + _from + ">\n");
	if (_to.count() > 0) {
		data.append("To: ");
		for (int i = 0; i < _to.count(); i++) {
			data.append("<" + _to.at(i) + ">" + ",");
		}
		data.append("\n");
	}
	if (_cc.count() > 0) {
		data.append("Cc: ");
		for (int i = 0; i < _cc.count(); i++) {
			data.append(_cc.at(i) + ",");
			if (i < _cc.count() - 1) {
				data.append(",");
			}
		}
		data.append("\n");
	}

	data.append("Subject: " + _subject + "\n");
	data.append(_body + "\n");

	_lastMailData = data;
	return data;
}



void MailSender::errorReceived(QAbstractSocket::SocketError socketError) {
	QString msg;

	switch (socketError) {
	case QAbstractSocket::ConnectionRefusedError:
		msg = "ConnectionRefusedError";
		break;
	case QAbstractSocket::RemoteHostClosedError:
		msg = "RemoteHostClosedError";
		break;
	case QAbstractSocket::HostNotFoundError:
		msg = "HostNotFoundError";
		break;
	case QAbstractSocket::SocketAccessError:
		msg = "SocketAccessError";
		break;
	case QAbstractSocket::SocketResourceError:
		msg = "SocketResourceError";
		break;
	case QAbstractSocket::SocketTimeoutError:
		msg = "SocketTimeoutError";
		break;
	case QAbstractSocket::DatagramTooLargeError:
		msg = "DatagramTooLargeError";
		break;
	case QAbstractSocket::NetworkError:
		msg = "NetworkError";
		break;
	case QAbstractSocket::AddressInUseError:
		msg = "AddressInUseError";
		break;
	case QAbstractSocket::SocketAddressNotAvailableError:
		msg = "SocketAddressNotAvailableError";
		break;
	case QAbstractSocket::UnsupportedSocketOperationError:
		msg = "UnsupportedSocketOperationError";
		break;
	case QAbstractSocket::ProxyAuthenticationRequiredError:
		msg = "ProxyAuthenticationRequiredError";
		break;
	default:
		msg = "Unknown Error";
	}

	error("Socket error [" + msg + "]");
}

bool MailSender::send(const QString& subject, const QString&body) {
	setSubject(subject);
	setBody(body);
	return send();
}

bool MailSender::send() {
	_lastError = "";

	if (_socket) {
		delete _socket;
	}

	_socket = new QTcpSocket(this);
	connect(_socket, SIGNAL(error(QAbstractSocket::SocketError)), this, SLOT(
			errorReceived(QAbstractSocket::SocketError)));


	_socket->connectToHost(_smtpServer, _port);

	if (!_socket->waitForConnected(_timeout)) {
		error("Time out connecting host");
		return false;
	}

	if (!read("220")) {
		return false;
	}

	if (!sendCommand("EHLO there", "250")) {
		if (!sendCommand("HELO there", "250")) {
			return false;
		}
	}


	if (!sendCommand(QString::fromLatin1("MAIL FROM:<") + _from
			+ QString::fromLatin1(">"), "250")) {
		return false;
	}

	QStringList recipients = _to + _cc ;
	QString strRecipients = "";
	for (int i = 0; i < recipients.count(); i++) {
		strRecipients += recipients.at(i) + ", ";
		if (!sendCommand(QString::fromLatin1("RCPT TO:<") + recipients.at(i)
				+ QString::fromLatin1(">"), "250")) {
			qDebug("rcpt error");
			return false;
		}
	}

	if (!sendCommand(QString::fromLatin1("DATA"), "354")) {
		return false;
	}
	if (!sendCommand(mailData() + QString::fromLatin1("\r\n."), "250")) {
		return false;
	}
	if (!sendCommand(QString::fromLatin1("QUIT"), "221")) {
		return false;
	}
	_socket->disconnectFromHost();

	return true;
}

bool MailSender::read(const QString &waitfor) {
	if (!_socket->waitForReadyRead(_timeout)) {
		error("Read timeout");
		return false;
	}

	if (!_socket->canReadLine()) {
		error("Can't read");
		return false;
	}

	QString responseLine;

	do {
		responseLine = _socket->readLine();
	} while (_socket->canReadLine() && responseLine[3] != ' ');

	_lastResponse = responseLine;

	QString prefix = responseLine.left(3);
	bool isOk = (prefix == waitfor);
	if (!isOk) {
		error("waiting for " + waitfor + ", received " + prefix);
		qDebug() << "Received a " << prefix << "where a" << waitfor
				<< " was expected.";
	}

	return isOk;
}

bool MailSender::sendCommand(const QString &cmd, const QString &waitfor) {
	QTextStream t(_socket);
	t << cmd + "\r\n";
	t.flush();
	_lastCmd = cmd;

	return read(waitfor);
}

void MailSender::error(const QString &msg) {
	_lastError = msg;
}

