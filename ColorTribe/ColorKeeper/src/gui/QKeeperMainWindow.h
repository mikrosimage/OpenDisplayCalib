/*
 * AppMainWin.h
 *
 *  Created on: 5 mars 2009
 *      Author: mfe
 */

#ifndef APPMAINWIN_H_
#define APPMAINWIN_H_

#include <QMainWindow>
#include <QSystemTrayIcon>

class QHeadedMainWidget;
class QAction;
class QMenu;
class QManagedApplication;
class QMiniToyWindow;

class QKeeperMainWindow: public QMainWindow {
Q_OBJECT
public:
	QKeeperMainWindow(QApplication&);
	virtual ~QKeeperMainWindow();
    void setVisible(bool visible);
    QWidget* getMainWidget();

protected:
    void closeEvent(QCloseEvent *event);

private slots:
     void quit();
     void systrayActivated(QSystemTrayIcon::ActivationReason reason);
     void showMessage(QString title, QString message,  bool weAreInTrouble);
     void showMiniToy();

private:

	void buildUI();
	void createTrayIcon();
	void createActions();
	QHeadedMainWidget * _mainWidget;
	QSystemTrayIcon *_trayIcon;
	QMenu *_trayIconMenu;
	QAction *_minimizeAction;
	QAction *_restoreAction;
	QAction *_quitAction;
	QAction *_showMiniToy;
	QApplication &_app;
	QMiniToyWindow * _miniToy;

};

#endif /* APPMAINWIN_H_ */
