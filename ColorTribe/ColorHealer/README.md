ColorHealer  
-------------------- 
ColorHealer is a measure and calibration tool. 

**Calibration**  

In this mode, ColorKeeper is used to display color patches at the right time.  
For that, you'll need to open a TCP/IP connection between ColorHealer and ColorKeeper (ColorCalib tab of ColorKeeper). 
   
Calibration protocol has been developped in collaboration with ENS Louis Lumière school. 
This protocol allow the calibrator to adjust OSD settings of a screen to match a chosen standard (ex : REC709) and process a 2D LUT that ColorKeeper will use to correct screen gamma.

**Measure**  

You can use measure mode with or without ColorKeeper. Without, you'll have to display color patch by yourself, using Paint or Gimp for example.  
  
3 modes of measures are available :  
*  Single measure 
*  Continuous measure
*  Batch measures
In each mode, measures are displayed in a diagram (CIE 1931 or CIE 1976) and an HTML report can be exported.

**Supported probes**
* Konica-Minolta CS-200
* Klein K-10
* Datacolor Spyders (but you'll have to ask datacolor for a API autorization key

ColorHealer used to support EyeOne display and EyeOne Pro but we're not allowed to share this code in the opensource project.