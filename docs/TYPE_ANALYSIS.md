# TYPE_ANALYSIS.md — ניתוח טיפוס סטטי מול טיפוס דינמי

**שם:** בן ציון חקק  **ת.ז. / מספר סטודנט:** 331078410

| # | קובץ:שורה | הביטוי | טיפוס סטטי | טיפוס דינמי | מי מכריע — קומפיילר או אובייקט? | נימוק במשפט אחד |
|---|-----------|--------|-------------|--------------|-------------------------------|------------------|
| 1 | Yard.java:71 | `unit.dailyStorageFee()` | `CargoUnit` | תלוי ביחידה בפועל (למשל `RefrigeratedContainer`) | האובייקט | `dailyStorageFee()` אבסטרקטית ב-`CargoUnit` ונדרסת בכל תת־מחלקה, כך שה-JVM משגר לפי הטיפוס האמיתי בזמן ריצה. |
| 2 | Yard.java:87 | `client.discountPercent()` | `Client` | תלוי בלקוח בפועל (למשל `GovernmentClient`) | האובייקט | אותו עיקרון: הפרמטר מוצהר כ-`Client`, אך המימוש שרץ הוא של הדרג האמיתי שהועבר. |
| 3 | RefrigeratedContainer.java:42 | `super.dailyStorageFee()` | `StandardContainer` (נקבע ע"י `super`) | אין שיגור דינמי — קשירה סטטית ל־`StandardContainer` | הקומפיילר | קריאת `super.` עוקפת את מנגנון השיגור הדינמי ונקשרת בזמן קומפילציה למימוש שבמחלקת האב המיידית, גם כשה-`this` בפועל הוא `RefrigeratedContainer`. |
| 4 | TerminalApp.java:44 | `unit instanceof LiquidTank liquidTank` | `CargoUnit` (לפני הבדיקה) | נבדק בזמן ריצה מול הטיפוס הדינמי בפועל | שניהם: הקומפיילר מתיר רק המרה בתוך ההיררכיה, וזמן הריצה קובע אם התנאי אמת | זו המרה למטה (downcast) עם pattern matching — המשתנה `liquidTank` מקבל טיפוס סטטי `LiquidTank` רק בתוך גוף ה-`if`, לאחר בדיקה בפועל. |
| 5 | TerminalApp.java:45 | `liquidTank.transferOut(3000.0)` | `LiquidTank` | `LiquidTank` | הקומפיילר | `transferOut` מוגדרת רק ב-`LiquidTank`. לו הקריאה נעשתה על `unit` (טיפוס `CargoUnit`) בלי ה-`instanceof`, הקומפיילר היה פוסל עם `cannot find symbol` — כי `transferOut` אינה חלק מהחוזה של `CargoUnit`. |
| 6 | StandardContainer.java:47 | `super.toString()` | `CargoUnit` (נקבע ע"י `super`) | `CargoUnit` בזמן הקישור, אך תוך כדי הריצה `handlingCategory()` עדיין משוגרת דינמית | הקומפיילר בוחר איזה מימוש `toString()` ירוץ; בתוך אותו מימוש, `handlingCategory()` עדיין מוכרעת ע"י האובייקט | הדגמה לשילוב: `super.` נקשר סטטית, אבל השדה הפולימורפי שבתוך אותו קוד (`CargoUnit.toString()` קורא ל-`handlingCategory()`) ממשיך להישלח דינמית ל-"Standard". |
| 7 | LiquidTank.java:75 | `super.toString()` | `CargoUnit` (נקבע ע"י `super`) | `CargoUnit` בזמן הקישור | הקומפיילר | דוגמה נוספת ל-`super.` — כאן ה-super המיידי של `LiquidTank` הוא `CargoUnit` עצמו (לא `StandardContainer`), כי `LiquidTank` יורשת ישירות ממנו. |
| 8 | Client.java:50 | `clientTier()`, `discountPercent()` (בתוך `toString()`) | `Client` (השיטות מוגדרות ומוצהרות שם) | תלוי באובייקט בפועל (`Client`/`ContractClient`/`GovernmentClient`) | הקומפיילר בוחר את **החתימה** (לפי הטיפוס הסטטי `Client`), האובייקט בוחר את **המימוש** | זו גם התשובה לשאלה המשלימה ב': הקומפיילר קובע שהקריאה חוקית לפי מה שמוצהר ב-`Client`, וה-JVM מחליט בזמן ריצה איזה גוף בפועל להריץ. |

---

### שאלה משלימה א׳
ב־`TerminalApp` (שורה 44) השתמשתי ב-`instanceof LiquidTank` בסבב הניקוז, למרות שב-`Yard` השימוש ב-`instanceof` אסור לחלוטין.

ההמרה מוצדקת דווקא כאן כי `transferOut(litres)` היא פעולה **שאינה חלק מהחוזה הכללי** של `CargoUnit` — היא קיימת אך ורק ב-`LiquidTank`, ורק מיכלי נוזלים ניתנים לניקוז. `TerminalApp` הוא שכבת ההדגמה/היישום הספציפית, לא מנוע פולימורפי כללי כמו `Yard` — היא *מותרת* לדעת שיש מיכלים בעולם. `Yard`, לעומת זאת, נועדה במפורש לצמצום ידע: היא לא אמורה לדעת בכלל שקיים דבר כזה `LiquidTank`.

כדי להיפטר מה-`instanceof` הזה לגמרי, הייתי צריך להעביר את פעולת הניקוז ל-`CargoUnit` עצמה כפעולה פולימורפית — למשל `drainable()` שמחזירה `false` כברירת מחדל ונדרסת ב-`LiquidTank` בלבד להחזיר `true` ולבצע את הניקוז בפועל. זה היה מעביר את ההחלטה "מי יודע לנקז" מהקוד הקורא (`TerminalApp`) אל האובייקט עצמו — בדיוק העיקרון שעומד מאחורי `Yard`.

---

### שאלה משלימה ב׳
Client.java:50 — `clientTier(), clientId, name, discountPercent());` (בתוך `toString()` שמוגדר ב-`Client`).

הקומפיילר בוחר את **החתימה**: מכיוון ש-`toString()` מוגדרת בתוך `Client`, הקומפיילר מוודא רק שקיימות שיטות `clientTier()` ו-`discountPercent()` בחתימה המוצהרת ב-`Client` (`String clientTier()`, `double discountPercent()`), ובזה תפקידו מסתיים. **האובייקט** בוחר את **המימוש**: כש-`toString()` רץ בפועל על מופע של `GovernmentClient`, הקריאות `clientTier()` ו-`discountPercent()` (שקוראות ל-`this` המרומז) משוגרות דינמית לגרסאות שנדרסו ב-`GovernmentClient` (`"Government"`, `25.0`) ולא לגרסאות ברירת המחדל שב-`Client`.
