# DESIGN.md — מסמך החלטות תכן

**שם:** בן ציון חקק  **ת.ז. / מספר סטודנט:** 331078410

---

### 1. האם `Client` צריכה להיות מחלקה אבסטרקטית?

השארתי את `Client` קונקרטית (Client.java:9). ההבדל המהותי בין שתי ההיררכיות: ל-`CargoUnit` אין שום ברירת מחדל הגיונית — "יחידת מטען כללית" היא מושג ריק, ולכן היא מוגדרת אבסטרקטית וכל שלוש הפעולות (`dailyStorageFee`, `handlingCategory`, `safetyBriefing`) הן אבסטרקטיות (CargoUnit.java:81-87), כדי לאלץ כל תת־מחלקה לענות בעצמה. לעומת זאת, "לקוח מזדמן" (walk-in) הוא ישות אמיתית וקיימת בעולם — לקוח שמגיע בפעם הראשונה בלי חוזה ובלי מעמד ממשלתי. ל-`Client` יש התנהגות ברירת מחדל שלמה ומשמעותית: הנחה 0% (Client.java:34-36), דרג `"Standard"` (Client.java:38-40), ואין טיפול מועדף (Client.java:43-45). מכיוון שיש תשובה סבירה וממשית לכל שאלה, אין סיבה מבנית לאסור יצירת מופע ישיר — `Client` הקונקרטית *היא* בדיוק לקוח ברירת המחדל בהיררכיה, לא "בסיס תיאורטי" גרידא.

---

### 2. מדוע `totalStorageCharge()` מוגדרת `final`?

`totalStorageCharge()` (CargoUnit.java:92-94) מממשת את הכלל העסקי הקבוע `dailyStorageFee() * daysStored` — זהה לכל סוגי המטען בנמל. אילו תת־מחלקה יכלה לדרוס אותה, היא הייתה יכולה לעקוף את כלל החיוב עצמו (למשל לחשב לפי `daysStored * 2`, או להתעלם מ-`dailyStorageFee()` לגמרי) — מה שהיה שובר את ההפרדה בין "מה קבוע" (הנוסחה) ל"מה משתנה" (התעריף היומי). סימון `final` חוסם זאת כבר בזמן קומפילציה. השיטה האבסטרקטית `dailyStorageFee()` (CargoUnit.java:81) היא "החור" שתת־המחלקות ממלאות, בעוד `totalStorageCharge()` היא השלד הקבוע סביבו — זו תבנית **Template Method**.

---

### 3. `RefrigeratedContainer` מול `HazmatContainer`

RefrigeratedContainer.java:42 — `return super.dailyStorageFee() + TariffPolicy.POWER_RATE * powerDrawKw;` (מוסיפה).
HazmatContainer.java:39 — `return super.dailyStorageFee() * TariffPolicy.HAZMAT_MULTIPLIER;` (מכפילה).

שתיהן קוראות ל-`super.dailyStorageFee()` במקום לחשב `BASE_STORAGE_RATE * volumeM3` מחדש, כי נוסחת "תעריף בסיס לפי נפח" שייכת ל-`StandardContainer` בלבד. אילו שוכפלה כאן, כל שינוי עתידי בנוסחת הבסיס היה מחייב תיקון בשלוש מחלקות בו־זמנית ועלול להישכח באחת מהן ולסטות. `super.dailyStorageFee()` מבטיח מקור אמת יחיד: כל תת־מחלקה מוסיפה רק את מה שייחודי לה מעל מה שהאב כבר יודע לחשב.

---

### 4. מדוע `LiquidTank` אינה יורשת מ־`StandardContainer`?

`LiquidTank` יורשת ישירות מ-`CargoUnit` (LiquidTank.java:14). מבחן ה-IS-A: "כל `LiquidTank` הוא `StandardContainer`?" — לא. `StandardContainer` מוגדרת לפי `volumeM3` קבוע וחיוב לפי נפח מלא (StandardContainer.java:30-32), בעוד מיכל נוזלים מחויב לפי `currentLitres()` בפועל — כמות משתנה שתלויה באחוז המילוי (LiquidTank.java:59-61). אילו `LiquidTank` ירשה מ-`StandardContainer`, היא הייתה יורשת גם שדה `volumeM3` שאינו רלוונטי לה וגם נוסחת חיוב שגויה שהייתה חובה לדרוס לחלוטין בלי כל שימוש ב-`super` — כלומר ירושה בלי שום שימוש חוזר אמיתי, רק כדי "להיות באותו עץ מחלקות". זה בדיוק המקרה שבו ירושה אינה מתאימה.

---

### 5. `getUnits()` — רשימה פנימית או עותק?

בחרתי להחזיר עותק: `return new ArrayList<>(units);` (Yard.java:48-49). המחיר: כל קריאה יוצרת הקצאה חדשה בסיבוכיות O(n) — יקר יותר מהחזרת ההפניה הפנימית. התועלת: קוד קורא לא יכול להוסיף או להסיר יחידות ישירות דרך הרשימה המוחזרת ולעקוף את הוולידציה של `receive()` (בדיקת כפילות מזהה וקיבולת, Yard.java:56-63) — כלומר להפר את הכימוס. בהיקף המטלה, שבו קיבולת ומזהים ייחודיים הם חלק מהחוזה של `Yard`, החזרת ההפניה הפנימית הייתה הופכת את כל אותן בדיקות לבנות עקיפה.

---

### 6. ההחלטה הקשה

ב-`receive()` (Yard.java:52-65), מזהה כפול זורק `IllegalArgumentException` (Yard.java:58) כי הבעיה היא בארגומנט שהתקבל — קלט שגוי מצד הקורא, שניתן לתקן פשוט ע"י שליחת `unitId` אחר. חריגה מקיבולת זורקת `IllegalStateException` (Yard.java:62) כי הבעיה אינה בארגומנט עצמו (היחידה תקינה לגמרי) אלא במצב הפנימי של ה-`Yard` באותו רגע — הוא מלא. ההבחנה מסייעת לקוד הקורא להגיב נכון: `IllegalArgumentException` אומר "תקן את מה ששלחת", `IllegalStateException` אומר "נסה שוב במצב אחר". אילו נאלצתי לבחור סוג אחד לשני המקרים, הייתי בוחר `IllegalStateException` — גם כפילות מזהה היא במובן מסוים תלוית־מצב (אותה יחידה הייתה מתקבלת בהצלחה ב-`Yard` ריק) — אך הייתי מאבד את האבחנה הסמנטית שמסייעת לקוד קורא להבדיל בין "התיקון באחריותי" לבין "התיקון תלוי בזמן/מצב חיצוני".

---

### בונוס: מדוע `Inspectable` ממשק ולא פעולה ב-`CargoUnit`

מימשתי את הבונוס: `Inspectable` (Inspectable.java:10) עם `String inspectionNote()`, מומש רק ב-`RefrigeratedContainer` (RefrigeratedContainer.java:13, 60-62) וב-`HazmatContainer` (HazmatContainer.java:13, 58-61).

אילו הוספתי `inspectionNote()` כפעולה על `CargoUnit`, כל תת־מחלקה — כולל `StandardContainer`, `LiquidTank` ו-`OversizedCargo`, שאין להן שום דבר משמעותי לדווח בבדיקה — הייתה חייבת לממש אותה, לרוב עם החזרת מחרוזת ריקה או placeholder. זו בדיוק ההפרה שמבחן זה נועד לחדד: `Inspectable` אינה שלב בהיררכיית ה-IS-A של `CargoUnit` (אין משמעות ל"האם כל יחידת מטען ניתנת לבדיקה" — לא כל אחת כן), אלא **יכולת חוצה־היררכיה**: היא משותפת ל-`RefrigeratedContainer` ו-`HazmatContainer` (שהם אחים תחת `StandardContainer`) בלי שום קשר לענף העץ שלהם. ממשק מבטא בדיוק את זה — "מי שיכול לענות על השאלה הזו", בלי לכפות תשובה על מי שהשאלה לא רלוונטית לו.

חשוב מכך: הוספת הבונוס לא חייבה שום שינוי ב-`CargoUnit` וגם לא ב"כלל הברזל" של `Yard` — הפעולה `printInspectionNotes(ArrayList<Inspectable>)` (Yard.java:109-114) מקבלת רשימה מוכנה של אובייקטים מטיפוס `Inspectable` ומדפיסה אותם, בלי לבדוק בעצמה מי "ראוי" להיכלל בה. הסינון בפועל (`if (unit instanceof Inspectable inspectable)`) קורה ב-`TerminalApp.java:64` — שם, לא ב-`Yard`, כי `TerminalApp` כבר מורשית להכיר טיפוסים קונקרטיים (בדיוק כמו בסבב הניקוז), בעוד ש-`Yard` נשארת עיוורת לחלוטין לשאלה מי בכלל `Inspectable`. `run.sh check` עדיין חוזר `CLEAN` על `Yard.java` אחרי השינוי הזה.
