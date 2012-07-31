#include "com_konicaminolta_cs200_CS200.h"

#include <stdio.h> 
#include <jni.h>
#include <iostream>
#include <Windows.h>
#include <iostream>
#include <sstream>
#include <iomanip>

static const int TRISTIMULUS_SIZE = 3;

/********CS-200 instantiation********/
typedef INT (CALLBACK*USB_INI)(INT);
typedef INT (CALLBACK*USB_NUM)(VOID);
typedef INT (CALLBACK*USB_IO)(INT,LPSTR,INT,INT);
typedef unsigned char UINT8;

HINSTANCE hDll = LoadLibrary("kmsecs200.dll");
USB_INI int_usb = (USB_INI) GetProcAddress(hDll, "int_usb");
USB_INI end_usb = (USB_INI) GetProcAddress(hDll, "end_usb");
USB_IO write64_usb = (USB_IO) GetProcAddress(hDll, "write64_usb");
USB_IO read64_usb = (USB_IO) GetProcAddress(hDll, "read64_usb");

char cBuf[250];
char cRemote[] = { "RMT,1\r\n" };
char cMdr[] = { "MDR,0\r\n" };
char cCSS[] = { "CSS,0\r\n" };
char cOBS[] = { "OBS,0\r\n" };
char cMes[] = { "MES,1\r\n" };
char cIDR[] = { "IDR\r\n" };
char cSCR[] = { "SCR\r\n" };
char cSPR[] = { "SPR\r\n" };
/***************/

void handleException(JNIEnv *env, jint line, char *source) {
	jthrowable exc = env->ExceptionOccurred();
	if (exc != NULL) {
		printf("Exception occurred in native code at line %d in file %s - \n",
				(int) line, source);
		env->ExceptionDescribe();
	}
}

//  jclass exception = env->FindClass("java/lang/IllegalArgumentException");
//  env->ThrowNew(exception, "thrown from C code");
//======================================================================================
void setupException(JNIEnv *env, int code, jstring error) {
	//note sure if we leak some memory here !!
	jclass exceptionClass = env->FindClass(
			"com/konicaminolta/cs200/CS200Exception");
	handleException(env, __LINE__, __FILE__);
	jmethodID method = env->GetMethodID(exceptionClass, "<init>",
			"(ILjava/lang/String;)V");
	handleException(env, __LINE__, __FILE__);

	jobject exception = env->NewObject(exceptionClass, method, code, error);
	handleException(env, __LINE__, __FILE__);
	env->Throw((jthrowable) exception);
}
void handleError(JNIEnv *jenv) {
	char code[4];
	strncpy(code, cBuf, 4);
	if (strcmp(code, "OK00") == 0) {

	} else if (strcmp(code, "OK03") == 0) {
		//		jstring message = jenv->NewStringUTF("OK03 : Low battery voltage.");
		//		setupException(jenv, 0, message);

	} else if (strcmp(code, "OK12") == 0) {
		//		jstring message = jenv->NewStringUTF(
		//				"OK12 : Lv, X,Y or Z exceeded display range.");
		//		setupException(jenv, 0, message);

	} else if (strcmp(code, "OK13") == 0) {
		//		jstring
		//				message =
		//						jenv->NewStringUTF(
		//								"OK13 : Low battery voltage and Lv, X,Y or Z exceeded display range.");
		//		setupException(jenv, 0, message);
	} else if (strcmp(code, "ER01") == 0) {
		jstring message = jenv->NewStringUTF("ER01 : Low battery voltage.");
		setupException(jenv, 1, message);

	} else if (strcmp(code, "ER02") == 0) {
		jstring message = jenv->NewStringUTF(
				"ER02 : No command is accepted while taking measurements.");
		setupException(jenv, 1, message);

	} else if (strcmp(code, "ER03") == 0) {
		jstring
				message =
						jenv->NewStringUTF(
								"ER03 : Invalid entry for Lvxy or Lvu’y’ during user calibration value or target value setup.");
		setupException(jenv, 1, message);

	} else if (strcmp(code, "ER05") == 0) {
		jstring message = jenv->NewStringUTF(
				"ER05 : Invalid entry for matrix calibration.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER06") == 0) {
		jstring
				message =
						jenv->NewStringUTF(
								"ER06 : Invalid matrix coefficient entry during matrix calibration (The elements on the main diagonal are below 0. The determinant is 0.)");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER07") == 0) {
		jstring message = jenv->NewStringUTF(
				"ER07 : CH00 does not accept user setup.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER08") == 0) {
		jstring
				message =
						jenv->NewStringUTF(
								"ER08 : Incorrect observer setting. Ex: Observer setting for the CH selected differs from observer setting of the instrument, the color space of “LvT?uv” is selected when 10°observer was selected, etc.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER09") == 0) {
		jstring
				message =
						jenv->NewStringUTF(
								"ER09 : Data protection is ON. MEM command to save the current data failed.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER10") == 0) {
		jstring message = jenv->NewStringUTF("ER10 : No command.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER11") == 0) {
		jstring message = jenv->NewStringUTF(
				"ER11 : Inbound data exceeds 64 characters.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER14") == 0) {
		jstring message = jenv->NewStringUTF(
				"ER14 : Incorrect parameter format.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER15") == 0) {
		jstring message = jenv->NewStringUTF(
				"ER15 : Incorrect parameter range or parameter error.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER16") == 0) {
		jstring
				message =
						jenv->NewStringUTF(
								"ER16 : Invalid operation of communication commands. For example: instrument isn’t in remote mode; user calibration procedure not completed correctly; sending CNT command with CDR or CDS isn’t appropriate; etc.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER20") == 0) {
		jstring message = jenv->NewStringUTF("ER20 : No data.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER21") == 0) {
		jstring message = jenv->NewStringUTF("ER21 : Low luminance.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER22") == 0) {
		jstring message = jenv->NewStringUTF(
				"ER22 : Beyond the range of measurement.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER23") == 0) {
		jstring message = jenv->NewStringUTF(
				"ER23 : Offset error (Shutter error).");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER27") == 0) {
		jstring
				message =
						jenv->NewStringUTF(
								"ER27 : Unstable range (due to excessive luminance variation).");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER30") == 0) {
		jstring
				message =
						jenv->NewStringUTF(
								"ER30 : Position of measuring angle selector isn’t correct. The angle of measurement was changed during measurement.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER31") == 0) {
		jstring message = jenv->NewStringUTF("ER31 : FROM write error.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER34") == 0) {
		jstring message = jenv->NewStringUTF("ER34 : Clock IC error.");
		setupException(jenv, 1, message);
	} else if (strcmp(code, "ER35") == 0) {
		jstring message = jenv->NewStringUTF("ER35 : A/D conversion error.");
		setupException(jenv, 1, message);
	} else {
		std::string message = "Unknown message : ";
		message.append(code);
		setupException(jenv, 1, jenv->NewStringUTF(message.c_str()));
	}

}
JNIEXPORT jboolean JNICALL Java_com_konicaminolta_cs200_CS200_isConnected(
		JNIEnv * jenv, jobject jobj) {
	jboolean result = false;
	int_usb(0);
	write64_usb(0, cIDR, 1, sizeof(cIDR));
	read64_usb(0, cBuf, 1, 250);
	if ((cBuf[0] == 'O' && cBuf[1] == 'K')
			|| (cBuf[0] == 'E' && cBuf[1] == 'R'))
		result = true;
	return result;
}

JNIEXPORT jstring JNICALL Java_com_konicaminolta_cs200_CS200_getSerialID(
		JNIEnv *jenv, jobject jobj) {
	write64_usb(0, cIDR, 1, sizeof(cIDR));
	read64_usb(0, cBuf, 1, 250);
	handleError(jenv);
	return jenv->NewStringUTF(cBuf);
}

JNIEXPORT jint JNICALL Java_com_konicaminolta_cs200_CS200_init(JNIEnv *env,
		jobject obj) {
	int_usb(0);
	write64_usb(0, cRemote, 1, sizeof(cRemote));
	read64_usb(0, cBuf, 1, 250);
	handleError(env);
	return 0;
}

JNIEXPORT void JNICALL Java_com_konicaminolta_cs200_CS200_calibrate
(JNIEnv *jenv, jobject jobj, jint observer, jint colorspace) {
	int_usb(0);

	write64_usb(0,cCSS,1,sizeof(cCSS));

	read64_usb(0,cBuf,1,250);
	handleError(jenv);

	write64_usb(0, cOBS,1,sizeof( cOBS));
	read64_usb(0,cBuf,1,250);
	handleError(jenv);
}

JNIEXPORT void JNICALL Java_com_konicaminolta_cs200_CS200_startMeasurement
(JNIEnv *jenv, jobject jobj) {
	write64_usb(0,cMes,1,sizeof(cMes));
	read64_usb(0,cBuf,1,250);
	handleError(jenv);
}

JNIEXPORT void JNICALL Java_com_konicaminolta_cs200_CS200_closeMeasurement
(JNIEnv *jenv, jobject jobj) {
	char cRemote2[] = {"RMT,0\r\n"};
	write64_usb(0,cRemote2,1,sizeof(cRemote2));
	read64_usb(0,cBuf,1,250);
	handleError(jenv);
	end_usb(0);

}

JNIEXPORT jstring JNICALL Java_com_konicaminolta_cs200_CS200_getTriStimulus(
		JNIEnv *jenv, jobject jobj) {
	while (1) {
		write64_usb(0, cMdr, 1, sizeof(cMdr));
		read64_usb(0, cBuf, 1, 250);

		std::string str(cBuf);
		if (!str.find("ER02")) {
			//measuring
		} else {
			//ok or Er
			return jenv->NewStringUTF(cBuf);
		}
	}
	handleError(jenv);
	return jenv->NewStringUTF("");
}

JNIEXPORT void JNICALL Java_com_konicaminolta_cs200_CS200_setSyncAndFrequency
(JNIEnv *jenv, jobject jobj, jint synchroValue, jint frequency) {
	std::ostringstream os;
	os<<std::setfill('0');
	os<<"SCS,"<<synchroValue<<","<<std::setw(5)<<frequency<<"\r\n";
	char* cSCS = const_cast<char*>( os.str().c_str());
	write64_usb(0, cSCS, 1, os.str().size()*sizeof(char));
	read64_usb(0,cBuf,1,250);
	delete cSCS;
}

JNIEXPORT jstring JNICALL Java_com_konicaminolta_cs200_CS200_getSyncAndFrequency(
		JNIEnv * jenv, jobject jobj) {
	write64_usb(0, cSCR, 1, sizeof(cSCR));
	read64_usb(0, cBuf, 1, 250);
	handleError(jenv);
	return jenv->NewStringUTF(cBuf);

}
JNIEXPORT void JNICALL Java_com_konicaminolta_cs200_CS200_setSpeed
(JNIEnv *jenv, jobject jobj, jint mode, jint duration) {
	std::ostringstream os;
	os<<std::setfill('0');
	os<<"SPS,"<<mode<<","<<std::setw(2)<<duration<<"\r\n";
	char* cSPS = const_cast<char*>( os.str().data());
	write64_usb(0, cSPS, 1, os.str().size()*sizeof(char));
	read64_usb(0,cBuf,1,250);

}

JNIEXPORT jstring JNICALL Java_com_konicaminolta_cs200_CS200_getSpeed(
		JNIEnv * jenv, jobject jobj) {
	write64_usb(0, cSPR, 1, sizeof(cSPR));
	read64_usb(0, cBuf, 1, 250);
	handleError(jenv);
	return jenv->NewStringUTF(cBuf);
}
