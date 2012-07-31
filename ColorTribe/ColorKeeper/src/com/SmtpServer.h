/*
 * smtp.h
 *
 *  Created on: Jan 9, 2012
 *      Author: mfe
 */

#ifndef SMTP_H
#define SMTP_H

#include <QStringList>
#include <QPair>
#include <stdexcept>
#include <QSslSocket>
#include <QTcpSocket>

#include "SmtpAuthData.h"

typedef QList <QPair <QString, QString> > ReceiversList;

class Letter : public QObject, private SmtpAuthData
{
    QString header;
    QString text;
    QString subject;
    QString encoding;
    ReceiversList receiversList;
    ReceiversList blindReceiversList;
    QStringList attachementFilesList;
    QString smtpItself;

    SmtpAuthData smtpData;

    // socket for ssl encrypted connection
    QSslSocket sslSocket;
    // stream that operate of protocol
    QTextStream stream;
    quint16 blockSize;

    Q_OBJECT


public:
    Letter(const SmtpAuthData &smtpAuthData);
    ~Letter();
    void setSmtpAuth(const SmtpAuthData &smtpAuthData);
    // set encoding
    void set_encoding(QString encode);
    // enable or disable ssl encrypting
    void set_sslEncrypting(bool enable);
    const QString& get_encoding() const { return encoding; }
    // set letter text
    void set_text(QString text);
    void set_subject(QString subj);
    // add attachement to file
    void set_attachement(const QStringList &list = QStringList());
    // get letter text
    QString get_text() { return text; }
    // set receivers
    void set_receivers(QString receivers);
    // set blind copy receivers
    void set_blindCopyReceivers(QString bl_receivers = QString());


    // send Letter
    void send();

   static QString encodeToBase64(QString line);

private:

    const QString FILE_SEPARATOR;

    enum RequestState { RequestState_Init, RequestState_AUTH, RequestState_AutorizeLogin,
                        RequestState_AutorizePass,
                    RequestState_From, RequestState_To, RequestState_BlindCopy,
                    RequestState_Data,
                RequestState_Mail, RequestState_Quit, RequestState_AfterEnd} Request;


    void on_connect();
    void send_request(QString line);
    void set_header();
    QByteArray& chunk_split(QByteArray &fileContent, int chunklen = 76);
    QString extractFileName(const QString &fullName);
    void addMoreRecipients(const QString &serverSays);
    void establishConnectionToSocket(int port);
    bool isEnabledSsl() const;

    QByteArray *p_FilesContent;
    int filesCount;
    bool recipientsExist;


private slots:
    void error_happens(QAbstractSocket::SocketError socketError);
    void sslError_happens(const QList<QSslError> &sslErrors);
    void on_read();
    void ready();

signals:
    void sendingProcessState(QString stateString, int duration = 1);

};


#endif // SMTP_H
