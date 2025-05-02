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

## Convert BLS Bowler History Export
1. Open the application
2. Select input files by choosing one of the following ways:
    * Drag & Drop a selection of files or a folder into the outlined area
    * Select multiple files by clicking the "Browse Files" button and using the file chooser
    * Select a folder by clicking the "Browse Folders" button and using the folder chooser
3. (Optional) Click on the "Save as..." button and choose an alternate output location and file name
    * The default location is the parent folder of the files selected or in the folder that was selected
    * The default file name follows the following pattern: "BowlerStats_yyyyMMdd'T'HHmmss.xlsx"
4. Hit the "Start!" button
    * Wait for the files to process
5. Click "OK" when the confirmation box appears
    * Note: the application will reset after clicking "OK"
6. Exit the application; click the "x" in the upper right corner :)

---

## Application Development (devs only)
Below are the environment and commands to build, run, and package the application

### Environment
Originally developed using:
* OpenJDK - 21.0.6
* Apache Maven - 3.9.6

### Build and Run Application
```
$ mvn clean javafx:run
```

### Package Application (custom JDK and fat jar)
```
$ mvn clean package
```
