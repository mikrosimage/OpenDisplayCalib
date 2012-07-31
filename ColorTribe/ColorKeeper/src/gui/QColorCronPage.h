/*
 * QDisplayDevicePage.h
 *
 *  Created on: 10 mars 2009
 *      Author: mfe
 */

#ifndef QDISPLAYDEVICEPAGE_H_
#define QDISPLAYDEVICEPAGE_H_

#include <QWidget>

class QLutDisplay;
class QLabel;

class ZooperDisplayDevice;

class QColorCronPage: public QWidget {
	Q_OBJECT
public:
	QColorCronPage(const ZooperDisplayDevice & dispDev, QWidget *parent = 0);
	virtual ~QColorCronPage();
private:
	QLutDisplay * _lutDisplay;
	QLabel * profLavel;
	QLabel * statusLabel;
private slots:
	void correctionStatusChanged(unsigned int);

};

#endif /* QDISPLAYDEVICEPAGE_H_ */
