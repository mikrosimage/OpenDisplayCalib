/*
 * QBalloonDialog.h
 *
 *  Created on: 22 oct. 2009
 *      Author: mfe
 */

#ifndef QBALLOONDIALOG_H_
#define QBALLOONDIALOG_H_

#include <QDialog>
#include <QString>

class QBalloonDialog : public QDialog {
	Q_OBJECT
public:
	QBalloonDialog(QString title, QString message, int posX, int posY,  bool weAreInTrouble);
	virtual ~QBalloonDialog();
protected:
	void paintEvent(QPaintEvent *event);

private slots:
     void openURL(const QString &);

};

#endif /* QBALLOONDIALOG_H_ */
