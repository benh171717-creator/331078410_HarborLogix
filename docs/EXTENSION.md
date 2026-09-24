# EXTENSION.md — דוח מבחן ההרחבה (חלק ד׳)

**שם:** בן ציון חקק  **ת.ז. / מספר סטודנט:** 331078410

---

### 1. רשימת כל הקבצים ששיניתי או יצרתי בחלק ד׳

| קובץ | נוצר / שונה | מה בדיוק השתנה |
|------|--------------|-----------------|
| `src/harborlogix/cargo/OversizedCargo.java` | נוצר | מחלקה חדשה, יורשת ישירות מ-`CargoUnit`. שדות `lengthM` (חייב לעלות על 12.0), `needsHeavyCrane`. תעריף יומי `OVERSIZE_DAILY_FLAT + (lengthM * 5.0)`, קטגוריה `"Oversized"`. |
| `src/harborlogix/app/TerminalApp.java` | שונה | נוסף חלק 4: יצירת מופע `OversizedCargo` וקליטתו לאותו `Yard` קיים, ואז `printManifest()` מחדש. |

`Yard.java` **אינו** מופיע בטבלה — לא נדרש בו שום שינוי. קליטת `OversizedCargo` עבדה מול ה-`Yard` הקיים בלי לגעת בו, וגם עברה את `run.sh check` (OpenClosedCheck) נקי — זו ההוכחה בפועל שהתכן עומד בעקרון הפתוח/סגור.

---

### 2. פלט ההרצה

לפני קליטת `OversizedCargo` (מיד לאחר סבב הניקוז):
```
--- Manifest: Ashdod Terminal ---
Standard[id=U-1, owner=Ad Hoc Shipping, 5000.0 kg, 4 days][volume=25.0 m3] | Standard container - no special handling required.
Reefer[id=U-2, owner=Blue Wave Logistics, 8000.0 kg, 6 days][volume=30.0 m3][temp=-18.0, power=3.5 kW] | Reefer - maintain -18.0C, power draw 3.5 kW.
Hazmat[id=U-3, owner=Ministry of Transport, 7000.0 kg, 3 days][volume=20.0 m3][hazardClass=6, escort=true] | Hazard class 6 - escort required.
Tank[id=U-4, owner=Blue Wave Logistics, 12000.0 kg, 5 days][capacity=20000.0 L, fill=65.0%] | Liquid tank - currently 65.0% full (13000.0 L).
Total daily revenue: 971.60
```

אחרי קליטת `OversizedCargo`:
```
--- Manifest: Ashdod Terminal ---
Standard[id=U-1, owner=Ad Hoc Shipping, 5000.0 kg, 4 days][volume=25.0 m3] | Standard container - no special handling required.
Reefer[id=U-2, owner=Blue Wave Logistics, 8000.0 kg, 6 days][volume=30.0 m3][temp=-18.0, power=3.5 kW] | Reefer - maintain -18.0C, power draw 3.5 kW.
Hazmat[id=U-3, owner=Ministry of Transport, 7000.0 kg, 3 days][volume=20.0 m3][hazardClass=6, escort=true] | Hazard class 6 - escort required.
Tank[id=U-4, owner=Blue Wave Logistics, 12000.0 kg, 5 days][capacity=20000.0 L, fill=65.0%] | Liquid tank - currently 65.0% full (13000.0 L).
Oversized[id=U-5, owner=Ad Hoc Shipping, 15000.0 kg, 2 days][length=18.0 m, heavyCrane=true] | Oversized cargo - 18.0 m long, requires heavy crane.
Total daily revenue: 1161.60
```

---

### 3. שאלת ההבנה

ההחלטה איזה קוד ירוץ עבור `unit.handlingCategory()` (בתוך `Yard.printManifest()`, Yard.java:105 שקוראת בעקיפין ל-`toString()` ול-`safetyBriefing()` שמזכירים את הקטגוריה) מתקבלת **בזמן ריצה**, לא בזמן קומפילציה. בזמן הקומפילציה, הקומפיילר ידע רק דבר אחד: שהמשתנה `unit` הוא מטיפוס `CargoUnit`, ושלכל `CargoUnit` *מובטחת* מתודה בשם `handlingCategory()` (כי היא מוצהרת אבסטרקטית ב-CargoUnit.java:84) — אך הוא לא ידע, ולא היה צריך לדעת, שקיימת בכלל מחלקה בשם `OversizedCargo`. `Yard.class` קדם בזמן להיווצרות `OversizedCargo.class` לגמרי (הוא הודר ולא שונה מ-Part D), ובכל זאת עבד איתה נכון.

בזמן הריצה, כשה-JVM מגיע לשורה `unit.handlingCategory()` עבור אובייקט שהטיפוס הדינמי שלו הוא בפועל `OversizedCargo`, הוא מחפש בטבלת השיטות הווירטואלית (vtable) של המחלקה `OversizedCargo` בפועל, ומוצא שם את המימוש שמחזיר `"Oversized"` (OversizedCargo.java:44-46). זו בדיוק ההדגמה המעשית של שיגור דינמי (dynamic dispatch): הקוד הקורא (`Yard`) נכתב וקומפל *לפני* שהמחלקה הקונקרטית נכתבה, אך הוא ממשיך לעבוד איתה נכון כי הוא מסתמך אך ורק על החוזה שמבטיח `CargoUnit` — לא על שום ידיעה על תת־המחלקות שלו.
