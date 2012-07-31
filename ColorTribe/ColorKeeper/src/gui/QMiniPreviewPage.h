/*
 * QMiniPreviewPage.h
 *
 *  Created on: 20 juil. 2009
 *      Author: mfe
 */

#ifndef QMiniPreviewPage_H_
#define QMiniPreviewPage_H_

#include <QWidget>

class ZooperDisplayDevice;
class QComboBox;
class QLutDisplay;


class QMiniPreviewPage: public QWidget {
Q_OBJECT
public:
	QMiniPreviewPage(const ZooperDisplayDevice & dispDev, QWidget *parent = 0);
	virtual ~QMiniPreviewPage();
private:
	QComboBox * _customLuts;
	QLutDisplay * _lutDisplay;
	unsigned int _OSScreenIndex;

private slots:
void customLutChanged(const QString & name);
};

#endif /* QMiniPreviewPage_H_ */
