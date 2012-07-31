/*
 * GuiLoader.cpp
 *
 *  Created on: 9 fevr. 2009
 *      Author: mfe
 */

#include "GuiLoader.h"
#include "QKeeperMainWindow.h"
#include "../core/ColorKeeperModel.h"
#include "../com/LockServer.h"
#include "QCalibWindow.h"
#include "QManagedApplication.h"

#include <string>
#include <exception>
#include <signal.h>
#include <unistd.h>
#include <QFile>
#include <QPixmap>
#include <QSplashScreen>
#include <QMessageBox>
#include <QSystemTrayIcon>
#include <QSettings>
#include <QSharedMemory>
#include <QTPlugin>
#include <QFile>
#include <QString>
#include <QStringList>
///smtp / smtpAuthData
#include <QtCore/QCoreApplication>
#include <QTextCodec>
#ifdef __APPLE__
Q_IMPORT_PLUGIN( qjpeg)
#endif

GuiLoader::GuiLoader() :
	_isLoaded(false) {

}

GuiLoader::~GuiLoader() {

}

int GuiLoader::openGUI(int argc, char *argv[]) {

	QManagedApplication a(argc, argv);

	QMessageBox msgBox;

	std::string lockFilePath = ColorKeeperModel::getLockFilePath("lock");
	LockServer server;
	if (!server.isServerUp() ) {
		msgBox.setText(
				"ColorKeeper is already running (have a look in your sys tray).<br>If you're sure ColorKeeper isn't running (may happen in case of session crash),<br>you'll find some help in the <a href=https://sites.google.com/a/mikrosimage.eu/colortribe/trouble-shootings>trouble-shootings section on ColorTribe web site. ");
		msgBox.exec();
		exit(1);
	}
	QFile lockFile(QString::fromStdString(lockFilePath));
	if (lockFile.open(QIODevice::WriteOnly))
		lockFile.close();
	ColorKeeperModel::shouldDelete = true;

	if (!QSystemTrayIcon::isSystemTrayAvailable()) {
		msgBox.setText(
				"I couldn't detect any system tray and ColorKeeper needs one.<br>Please check if you didn't remove it.<br>You'll find some help in the <a href=https://sites.google.com/a/mikrosimage.eu/colortribe/trouble-shootings>trouble-shootings section on ColorTribe web site</a>.");
		msgBox.exec();
		exit(1);
	}

	QApplication::setQuitOnLastWindowClosed(false);
	QPixmap pixmap("./img/splash.png");
	QSplashScreen splash(pixmap);
	splash.show();
	QString message = "Version ";
	message.append(QString::number(ColorKeeperModel::version_major));
	message.append(QString(ColorKeeperModel::version_minor.c_str()));

	splash.showMessage(message, Qt::AlignBottom | Qt::AlignLeft, QColor(255,
			255, 255));

	QKeeperMainWindow mainWin(a);

	a.processEvents();

#ifdef __APPLE__
	QFile file("style/macOSStyle.qss");
#else
	QFile file("style/style.qss");
#endif
	if (file.open(QIODevice::ReadOnly)) {
		// Applique la CSS a la fenetre Qt
		QString styleSheet = QLatin1String(file.readAll());
		a.setStyleSheet(styleSheet);
		mainWin.setStyleSheet(styleSheet);
		file.close();
	}

	//mainWin.show();
	splash.finish(&mainWin);

	QCoreApplication::setOrganizationName("HD3D");
	QCoreApplication::setApplicationName("ColorKeeper");

	//	QString path = QCoreApplication::applicationDirPath() ;
	//	path.append("/../PlugIns");
	//QCoreApplication::addLibraryPath(path);
	//QCoreApplicatop,

	_isLoaded = true;

	ColorKeeperModel::Instance().sendAMailCalibrationRequest();
	return a.exec();

}

