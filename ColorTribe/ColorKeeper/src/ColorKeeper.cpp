/*
 * ColorKeeper.cpp
 *
 *  Created on: 5 fevr. 2009
 *      Author: mfe
 */

#include "gui/GuiLoader.h"
#include "core/ColorKeeperModel.h"

#include <boost/shared_ptr.hpp>
#include <cstdlib>
#include <exception>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <iostream>
#include <sstream>
#include <locale>
#include <QFile>
#include <QMessageBox>
#ifdef __WIN32__

#ifndef HAVE_DECL_SYS_SIGLIST
const char *const sys_siglist[] = {
	NULL, /* 0 */
	"SIGHUP", /* 1 */
	"SIGINT", /* 2 */
	"SIGQUIT", /* 3 */
	"SIGILL", /* 4 */
	"SIGTRAP", /* 5 */
	"SIGABRT", /* 6 */
	"SIGEMT", /* 7 */
	"SIGFPE", /* 8 */
	"SIGKILL", /* 9 */
	"SIGUSR1", /* 10 */
	"SIGSEGV", /* 11 */
	"SIGUSR2", /* 12 */
	"SIGPIPE", /* 13 */
	"SIGALRM", /* 14 */
	"SIGTERM", /* 15 */
	"SIGSTKFLT", /* 16 */
	"SIGCHLD", /* 17 */
	"SIGCONT", /* 18 */
	"SIGSTOP", /* 19 */
	"SIGTSTP", /* 20 */
	"SIGTTIN", /* 21 */
	"SIGTTOU", /* 22 */
};
#else
extern const char *const sys_siglist[];
#endif

#ifndef HAVE_STRSIGNAL
const char *
strsignal(int s)
{
	static char buf[100];
	if (s > 0 && s < (int)sizeof(sys_siglist) / (int)sizeof(sys_siglist[0]))
	return sys_siglist[s];
	sprintf(buf, ("Unknown signal %d"), s);
	return buf;
}
#endif

#endif

using namespace std;

typedef boost::shared_ptr<GuiLoader> ptrGUILoader;

bool alreadyDone = false;

void getOut() {
	if (!alreadyDone) {
		ColorKeeperModel::deleteLockFile();
		alreadyDone = true;
	}
}
void handleAtExit() {
	ColorKeeperModel::logMessage("atexit");
	getOut();
}
void handleSetTerminate() {
	ColorKeeperModel::logMessage("set_terminate");
	getOut();
}
void handleSetUnexpected() {
	ColorKeeperModel::logMessage("set_unexpected");
	getOut();
}

void getOut(int signum) {
	ostringstream os;
	os << "Receive signal : " << strsignal(signum) << endl;
	ColorKeeperModel::logMessage(os.str());
	getOut();
}

void getOutAndQuit(int signum) {
	getOut(signum);
	exit(signum);
}

int main(int argc, char **argv) {

	atexit(handleAtExit);
	std::set_terminate(handleSetTerminate);
	std::set_unexpected(handleSetUnexpected);

#ifdef __linux__
	signal(SIGTSTP, getOut);
	signal(SIGHUP, getOutAndQuit); //deconnexion
	signal(SIGCHLD, getOut);
	signal(SIGQUIT, getOutAndQuit);
#endif
	signal(SIGTERM, getOut);
	signal(SIGABRT, getOut);
	signal(SIGINT, getOutAndQuit);//ctrl-c
	signal(SIGSEGV, getOutAndQuit); // seg fault
	//signal(SIGKILL, getOut);//can't be catched
	setlocale(LC_ALL, "en.US_UTF-8");
	ColorKeeperModel::logMessage(":: ColorKeeper ::");
	ptrGUILoader guiLoader(new GuiLoader());
	guiLoader->openGUI(argc, argv);

}
