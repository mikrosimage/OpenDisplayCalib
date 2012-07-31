/*
 * QHeadBand.h
 *
 *  Created on: 23 mars 2009
 *      Author: mfe
 */

#ifndef QHEADBAND_H_
#define QHEADBAND_H_

#include <QWidget>

class QTabWidget;
class QDisplayDevicePanel;

class QHeadedMainWidget : public QWidget {
	Q_OBJECT
public:
	QHeadedMainWidget(QWidget *parent=0);
	virtual ~QHeadedMainWidget();
	QTabWidget *_tabWidget;
	QDisplayDevicePanel * _dispDevPan;
};

#endif /* QHEADBAND_H_ */
