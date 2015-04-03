/* Inspired from Read-Edid (c) 2000,2001,2002 John Fremlin */
/* patched by mfe for Linux 64 - 2009 */

#ifdef __linux__
#include "LRMIGetEDID.h"
#include "EDIDData.h"
#include <string.h>

#include <nv_control.h>
#include <NvCtrlAttributes.h>
//#include "NVCtrlLib.h"
#include <iostream>
#include <sys/io.h>
//#include <>

#ifdef __cplusplus
extern "C" {
#include <libx86.h>
}
#else
#include <libx86.h>
#endif

using namespace std;

int LRMIGetEDID::do_vbe_service(unsigned AX,unsigned BX,LRMI_regs* regs)
{
	const unsigned interrupt = 0x10;
	regs->eax = AX;
	regs->ebx = BX;
	//Performing real mode VBE call
	if( !LRMI_int(interrupt, regs) )
	{
		//Error: something went wrong performing real mode interrupt
		return REAL_MODE_ERROR;
	}

	AX = regs->eax;

	if (!((AX & 0xff) == 0x4f))
	return REGISTER_ERROR;

	return SUCCESS;
}

int
LRMIGetEDID::read_edid( unsigned controller, vector<EDIDData> &edidDataVector )
{
	LRMI_regs regs;

	byte* block;
	//byte* buffer;
	byte* pointer;
	static unsigned int iteration = 0;
	block = (byte*)LRMI_alloc_real( EDID_BLOCK_SIZE );

	if ( !block )
	{
		//Error: can't allocate 0x%x bytes of DOS memory for output block EDID_BLOCK_SIZE
		return MEMORY_ERROR;
	}

	//buffer = block;

	unsigned counter;

	memset( block, MAGIC, EDID_BLOCK_SIZE );
	memset(&regs, 0, sizeof(regs));
	regs.es = ((unsigned long)block)>>4;
	regs.edi = ((unsigned long)block)& 0x0f;
	regs.ecx = controller;
	regs.edx = 1;//save state

	unsigned AX = 0x4f15;
	unsigned BX = 1;

	int res = do_vbe_service( AX, BX, &regs );

	bool found = false;
	for( pointer=block, counter=EDID_BLOCK_SIZE; counter; counter--,pointer++ )
	{
		if ( *pointer != MAGIC )
		found = true;
	}

	if(found && res==SUCCESS) { // EDID was found and VBE call ok
		EDIDData data(block, EDID_BLOCK_SIZE);
		if(data.isCheckSumOk()) {
			iteration = 0;
			edidDataVector.push_back(data);
			LRMI_free_real( block );
			return SUCCESS;
		} else {
			cout<<"checksum "<<iteration<<" : "<< data.getCheckSum()<<" "<< data.getCheckSum()%256<<endl;
			LRMI_free_real( block );
			iteration++;
			if( iteration > MAX_TRIES ) { // TODO JUST FOR TEST
				EDIDData voidData;
				edidDataVector.push_back(voidData);
				LRMI_free_real( block );
				return SUCCESS;
			}
			return read_edid( controller, edidDataVector );//EDID_CHECKSUM_UNCORRECT;
		}
	}
	else if(found && res != SUCCESS ) { // EDID found but may be corrupt (VBE call failed)
		LRMI_free_real( block );
		return res;
	}
	else { // EDID was not found, means no more EDID.
		LRMI_free_real( block );
		return OUT_BLOCK_UNCHANGE;
	}
}



int
LRMIGetEDID::getEDID(vector<EDIDData> &edidDataVector )
{
	int res = 0;

	if( !LRMI_init() )
	{
		res = LRMI_INIT_FAILED;
		dumpError(res);

	} else {
		ioperm(0, 0x400 , 1);
		iopl(3);
		unsigned control = 0;
		//while EDID block can be found
		while (!res) {
			cout<<"------- try block " <<control<<"--------"<<endl;
			res = read_edid(control,edidDataVector);
			///////
			dumpError(res);
			control++;
		}
	}

	//
	return res;
}

bool LRMIGetEDID::isGetEDIDEnable()
{
	struct LRMI_regs regs;
	if(!LRMI_init()) {
		return false;
	}
	memset(&regs, 0, sizeof(regs));
	regs.eax = 0x4f15;
	regs.ebx = 0x0000;
	regs.es = 0x3000;
	regs.edi = 0x3000;

	ioperm(0, 0x400, 1);
	iopl(3);

	if(LRMI_int(0x10, &regs) == 0) {
		cout<<"*** getEDID enable ***"<<endl;
		return false;
	}

	if((regs.eax & 0xff) == 0x4f) {
		cout<<"*** getEDID enable ***"<<endl;
		return true;

	} else {
		cout<<"*** getEDID NOT enable ***"<<endl;
		return false;
	}

}

void LRMIGetEDID::dumpError(int error) {

	switch (error) {
		case SUCCESS:
		break;
		case REAL_MODE_ERROR:
		cout<<"Real mode error"<<endl;
		break;
		case REGISTER_ERROR:
		cout<<"Register error"<<endl;
		break;
		case UNKNOW_SERVICE:
		cout<<"Unknown VBE/DDC service"<<endl;
		break;
		case MEMORY_ERROR:
		cout<<"Memory allocation Error"<<endl;
		break;
		case VBE_CALL_FAILED:
		cout<<"VBE call failed"<<endl;
		break;
		case OUT_BLOCK_UNCHANGE:
		cout<<"EDID not found (out block unchange)"<<endl;
		break;
		case LRMI_INIT_FAILED:
		cout<<"LRMI init failed"<<endl;
		break;
		case EDID_CHECKSUM_UNCORRECT :
		cout<<"EDID checksum uncorrect"<<endl;
		break;
		default:
		cout<<"Unknown code : "<<error<<endl;
		break;
	}
}
#endif

