# BLS Bowler History to XLSX
Helper application for converting BLS bowler history into XLSX (Excel)

The application is written in Java. It takes in a Bowler History export from
BLS, parses the weekly scores and creates an Excel row for each bowler in a
workbook.

The application performs the following:
* Parses the weekly columns
* Counts games bowled
* Counts series bowled
* Calculates final averages
* Exports data to Excel spreadsheet

BLS - developed by CDE Software, https://www.cdesoftware.com/.

---

## Requirements
* BLS Bowler History Export (see below)

---

## BLS Bowler History Export
 1. Open League in BLS
 2. Click 'Reports'
 3. Hover over 'Mid/End Season'
 4. Click 'Team & Bowler Histories'
 5. Check settings and make report selection in #3
 6. Select bowler history style Handicap report (112) or Scratch report (108)
 7. Click 'Print' -> 'Report'
 8. Select 'Microsoft Print to PDF' for printer name
 9. Click 'OK'
10. Once the 'Save Print Output as' dialog appears:
    1. Change if desired and make note of the save location
    2. Type in a 'File name'
    3. Make sure 'Save as type' is 'PDF Document (*.pdf)'
11. Click 'Save'

---

## Convert BLS Bowler History Export (TODO)
1. Open the application
2. Select input files by choosing one of the following ways:
    * Drag & Drop a selction of files or folder into the outlined area
    * Select muliple files using the "Browse Files" button and file chooser
    * Select folder using the "Browse Folders" button and folder chooser
3. (Optional) Click on the "Save as..." button and choose an alternate Excel (xlsx) output location and file name
    * The default location is in the parent folder of the files selected or in the folder that was selected
    * The default file name follows the following pattern "BowlerStats_yyyyMMdd'T'HHmmss.xlsx"
4. Hit the "Start!" button
5. Wait for the files to process
6. Click "OK" when the confirmation box appears
    * Note: the application will reset after clicking "OK"
7. Exit the application; click the "x" in the upper right corner :)
