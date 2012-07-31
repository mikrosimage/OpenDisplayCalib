/*
 * QColorToyTab.h
 *
 *  Created on: 20 mars 2009
 *      Author: mfe
 */

#ifndef QCOLORTOYTAB_H_
#define QCOLORTOYTAB_H_

#include <QWidget>

class QDisplayDevicePanel;
class QStackedWidget;
class QListWidgetItem;

class QColorToyTab : public QWidget {
	Q_OBJECT
public:
	QColorToyTab(QDisplayDevicePanel * dispDevPan, QWidget *parent = 0);
	QDisplayDevicePanel * _dispDevPan;
	QStackedWidget *_pagesWidget;
	virtual ~QColorToyTab();

	void createPages();

private slots:
     void changePage(QListWidgetItem *current, QListWidgetItem *previous);
};


#endif /* QCOLORTOYTAB_H_ */
