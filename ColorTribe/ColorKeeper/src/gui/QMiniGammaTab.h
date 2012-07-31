/*
 * QMiniGammaTab.h
 *
 *  Created on: 20 juil. 2009
 *      Author: mfe
 */

#ifndef QMINIGAMMATAB_H_
#define QMINIGAMMATAB_H_

#include <QWidget>

class QDisplayDevicePanel;
class QStackedWidget;
class QListWidgetItem;

class QMiniGammaTab : public QWidget {
Q_OBJECT
public:
	QMiniGammaTab(QDisplayDevicePanel * dispDevPan, QWidget *parent = 0);
	virtual ~QMiniGammaTab();

	QDisplayDevicePanel * _dispDevPan;
	QStackedWidget *_pagesWidget;

	void createPages();

private slots:
void changePage(QListWidgetItem *current, QListWidgetItem *previous);

};

#endif /* QMINIGAMMATAB_H_ */
