/*
 * ColorKeeperModel.h
 *
 *  Created on: 9 mars 2009
 *      Author: mfe
 */

#ifndef COLORKEEPERMODEL_H_
#define COLORKEEPERMODEL_H_

#include "../com/CKSocketClient.h"
#include "HealerCalibModel.h"
#include "ZooperLocalHost.h"
#include "../com/SmtpServer.h"
#include "../com/SmtpAuthData.h"
#include "../com/MailSender.h"
#include <LUT2DCorrection.h>
#ifdef __WIN32__ 
#include <WinLUTLoader.h>
#endif
#ifdef __APPLE__
#include <MacLUTLoader.h>
#endif
#ifdef __linux__
#include <GNULUTLoader.h>
#endif

#include <string>
#include <sstream>
#include <map>
#include <QObject>
#include <QStringList>
#include <QThread>
#include <QString>

class DisplayDevice;
class CronThread;

class ColorKeeperModel: public QObject {
Q_OBJECT
private:
	ColorKeeperModel();
	ColorKeeperModel(const ColorKeeperModel &);
	ColorKeeperModel & operator=(const ColorKeeperModel &);
	virtual ~ColorKeeperModel();
	ZooperLocalHost* _deviceInfo;
	CKSocketClient _socketClient;

	CronThread* cron;

#ifdef __WIN32__ 
	WinLUTLoader _lutLoader;
#endif
#ifdef __APPLE__
	MacLUTLoader _lutLoader;
#endif
#ifdef __linux__
	GNULUTLoader _lutLoader;
#endif

	std::map<unsigned int, LUT2DCorrection> _correctionLuts;
	std::map<unsigned int, LUT2DCorrection> _orignalLuts;
	QStringList _customLuts;
	QStringList _customLutsDescription;
	QStringList _customPatterns;
	std::vector<QPixmap> _patternsIcons;
	std::map<unsigned int, LUT2DCorrection> _selectedCustomLut;
	LUT2DCorrection *_default;
	std::vector<unsigned int> _screenOrder;
	std::vector<bool> _shouldApplyCorrection;
	std::vector<bool> _isCustomEDID;
	std::vector<QString> _ipAdressSetting;
	std::map<unsigned int, HealerCalibModel> _calib;
	void launchCron();
	float _calibWindowsZoom;
	int _calibWindowsTranslateX;
	int _calibWindowsTranslateY;

	static QString s_smtpServer;
	static QString s_mailFrom;
	static QString s_mailTo;
	const static QString s_unsetValue;
	MailSender * _mailer;

	static bool _isMailRequestEnable;
	MailSender* getMailer();

public:
	enum PrinterLights {
		RED, GREEN, BLUE
	};
	static ColorKeeperModel& Instance() {
		static ColorKeeperModel instance;
		return instance;
	}

	void sendAMailCalibrationRequest();
	static bool shouldDelete;
	void getOut();
	const ILocalHost& getDeviceInfo() const {
		return *(_deviceInfo);
	}

	const QStringList& getCustomLutsDescription() {
		return _customLutsDescription;
	}
	static float getBGColorFromGamma(const float & gamma);
	//unsigned char getDisplayDeviceNumber() const;
	const std::string& getHostName() const;
	//const DISPDEV_VECTOR& getDisplayDevices() const;
	const CKSocketClient& getSocketServer() const;
	void startSocketServer(const QString &serverAddress,
			unsigned int osScreenIndex);
	//	const DisplayDevice
	//			& getDisplayDeviceFromOSIndex(const unsigned int &index) const;
	void initCorrections();
	void initCustomLuts();
	void initCustomPatterns();
	void applyCorrection(const unsigned int &screenIndex) const;
	void setScreenProfilGamma(const unsigned int &screenIndex, float gamma);
	void setScreenProfilPL(const unsigned int &screenIndex, float plValue,
			PrinterLights whichPL);
	QString getIpAdress(const unsigned int &screenInde);
	void setIpAdress(const unsigned int &screenIndex, QString ipadress);
	void applyCorrections() const;
	bool isScreenCorrected(const unsigned int &index) const;
	unsigned int
	isScreenCorrectionHasBeen(const unsigned int &osScreenIndex) const;
	std::string getScreenProfilName(const unsigned int &index) const;
	std::string getScreenProfilNamePath(const unsigned int &osScreenIndex) const;
	std::string getScreenProfilPath(const unsigned int &index) const;
	const LUT2DCorrection& getScreenProfil(const unsigned int &index) const;
	const std::vector<unsigned int>& getScreenResOrder() const {
		return _screenOrder;
	}
	void setScreenShouldApplyCorrection(const unsigned int &screenIndex,
			bool should);
	bool shouldApplyCorrection(const unsigned int &screenIndex) const;
	bool addCalibModelForScreen(const unsigned int &screenIndex);
	bool setCalibSizeForScreen(const unsigned int &screenIndex,
			const unsigned int & size);
	bool addCalibValueForScreen(const unsigned int &screenIndex,
			const unsigned short &r, const unsigned short &g,
			const unsigned short &b);
	bool setCalibDoneForScreen(const unsigned int &screenIndex);
	bool shoudDisplayCalibForScreen(const unsigned int &screenIndex,
			const bool &should);
	bool updateCorrection(const unsigned int &screenIndex,
			const QString & infos);
	bool endCalib(const unsigned int &screenIndex);

	const QStringList& getCustomLuts() {
		return _customLuts;
	}
	const QStringList& getCustomPatterns() {
		return _customPatterns;
	}
	const std::vector<QPixmap>& getCustomPatternsIcons() {
		return _patternsIcons;
	}

	void setSelectedCustomLut(const unsigned int &screenIndex,
			const QString &selectedLut);
	const LUT2DCorrection
	* getSelectedCustomLut(const unsigned int &screenIndex);

	const static std::string CORRECTION_PATH;
	const static std::string CUSTOM_LUTS_PATH;
	const static std::string CUSTOM_PATTERNS_PATH;
	const static std::string LOCK_PATH;
	const static std::string CALIB_REQUEST_PATH;
	const static std::string INVALID_SCREEN_PATH;
	const static std::string MAILER_INI_PATH;
	void emitCorrectionChanged(unsigned int);
	void emitPopMessage(QString title, QString message, bool weAreInTrouble);

	const static unsigned int version_major = 2;
	const static std::string version_minor;

	const unsigned int getXScreensNumber() const {
		return _deviceInfo->getXScreensNumber();
	}

	const bool& isXScreenDisorder() const {
		return _deviceInfo->isXScreenDisorder();
	}
	const bool& isQTIndexDisorder() const {
		return _deviceInfo->isQTIndexDisorder();
	}

	QString getIniFilePath() const;
	void writeSettings() const;

	const static float s_gammaUpBound;
	const static float s_gammaDownBound;
	const static float s_PLUpBound;
	const static float s_PLDownBound;
	const static unsigned int s_ProfilTimeLimit;
	const static unsigned int s_CronTimingMinutes;

	static void logMessage(std::string message);
	static void logDebug(std::string message);
	float getCalibWindowsZoom() {
		return _calibWindowsZoom;
	}
	void setCalibWindowsZoom(float zoom);

	int getCalibWindowsTranslateX() {
		return _calibWindowsTranslateX;
	}
	int getCalibWindowsTranslateY() {
		return _calibWindowsTranslateY;
	}
	void setCalibWindowsTranslate(int transX, int transY);

	static std::string getLockFilePath(std::string end);
	static void deleteLockFile();

	static std::string getCalibRequestFilePath(std::string screenFullName);
	static void deleteCalibRequestFile(std::string screenFullName,
			const unsigned int &screenIndex);
	static void createCalibRequestAskedFile(std::string screenFullName,
			std::string message);
	static bool isCalibResquestAsked(std::string screenFullName);
	static bool isScreenModelInvalidate(std::string manufacturerName,
			std::string modelName);
	static bool isMachineInvalidate();
	static std::string getInvalidateFilePath(std::string manufacturerName,
			std::string modelName);
	static std::string getInvalidateFilePath(std::string machine);
	static void replaceSpaceCharacters(std::string &astring);

	signals:
	void correctionChanged(unsigned int);
	void correctionApplied(unsigned int);
	void measuresDone();
	void calibWindowZoomChanged();
	void calibWindowTransChanged();
	void gammaChanged(unsigned int, float);
	void
	plChanged(unsigned int, float, ColorKeeperModel::PrinterLights whichPL);
	void popMessage(QString title, QString message, bool weAreInTrouble);

};

class CronThread: public QThread

{
	bool runCron;
public:

	CronThread(QObject *parent) :

		QThread(parent) {
		runCron = true;
		start();
	}

	void stopCron() {
		runCron = false;
		exit(0);
		wait();
	}

	~CronThread() {
	}

	void run();

};

#endif /* COLORKEEPERMODEL_H_ */
