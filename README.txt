HarborLogix - מטלת סיום, תכנות מונחה עצמים ב-Java
==================================================

התחלה מהירה
-----------
1. פתחו src/harborlogix/ops/TariffPolicy.java
   החליפו "TODO_PUT_YOUR_ID_HERE" במספר הסטודנט שלכם (ספרות בלבד).

2. קמפלו:
      macOS / Linux:  ./build.sh
      Windows:        build.bat

3. הריצו את מבחן הקבלה:
      ./run.sh test        |    run.bat test

   ביום הראשון הוא ייכשל מיד עם UnsupportedOperationException. זה תקין.
   התקדמו TODO אחר TODO והריצו שוב.

4. בדקו את כלל הפתוח/סגור על Yard.java:
      ./run.sh check       |    run.bat check

5. הריצו את תוכנית ההדגמה שלכם:
      ./run.sh app         |    run.bat app


קבצים שאסור לשנות
------------------
  src/harborlogix/cargo/CargoUnit.java
  src/harborlogix/tests/AcceptanceTest.java
  src/harborlogix/tools/OpenClosedCheck.java

ב-TariffPolicy.java מותר לשנות אך ורק את השורה של STUDENT_ID.


המסמכים להגשה
--------------
  docs/DESIGN.md          - שישה סעיפי תכן, עד 900 מילים
  docs/TYPE_ANALYSIS.md   - ניתוח שמונה שורות מהקוד שלכם
  docs/EXTENSION.md       - דוח מבחן ההרחבה (חלק ד')

מלאו אותם בזמן העבודה, לא בסוף. הם דורשים הפניה לשורות אמיתיות בקוד שלכם.


לפני ההגשה
-----------
  [ ] הפרויקט מתקמפל מתוך ה-ZIP בתיקייה נקייה
  [ ] run test  -> 44 passed, 0 failed
  [ ] run check -> CLEAN
  [ ] שלושת המסמכים מלאים
  [ ] output.txt מצורף
  [ ] מחקתם את תיקיית out/ ואת קבצי ה-.class
