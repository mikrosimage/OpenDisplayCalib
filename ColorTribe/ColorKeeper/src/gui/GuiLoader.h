/*
 * GuiLoader.h
 *
 *  Created on: 9 fevr. 2009
 *      Author: mfe
 */

#ifndef GUILOADER_H_
#define GUILOADER_H_

#include <QApplication>

class GuiLoader : public QObject{
	Q_OBJECT
public:
	GuiLoader();
	virtual ~GuiLoader();
	int openGUI(int argc, char *argv[]);


private:
	bool _isLoaded;
};

#endif /* GUILOADER_H_ */
