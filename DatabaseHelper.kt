import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// هذه الفئة هي المسؤولة عن كل ما يتعلق بقاعدة البيانات
// ترث من SQLiteOpenHelper، وهي فئة مخصصة من أندرويد لتسهيل التعامل مع قواعد بيانات SQLite
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // companion object يستخدم لتعريف الثوابت التي ستكون متاحة على مستوى الفئة كلها
    companion object {
        private const val DATABASE_NAME = "LibraryCashier.db" // اسم ملف قاعدة البيانات
        private const val DATABASE_VERSION = 1 // إصدار قاعدة البيانات. إذا قمت بتغييره، سيتم استدعاء onUpgrade

        // تعريف أسماء الجداول لتجنب الأخطاء الإملائية
        const val TABLE_PRODUCTS = "Products"
        const val TABLE_SALES = "Sales"
        const val TABLE_SALE_ITEMS = "Sale_Items"
        const val TABLE_BOOK_LOANS = "Book_Loans"

        // تعريف أعمدة جدول المنتجات (Products)
        const val COL_PRODUCT_ID = "product_id"
        const val COL_PRODUCT_NAME = "name"
        const val COL_PRODUCT_CATEGORY = "category"
        const val COL_PRODUCT_PRICE = "price"
        const val COL_PRODUCT_QUANTITY = "quantity"
        const val COL_PRODUCT_BARCODE = "barcode"
        const val COL_PRODUCT_NOTES = "notes"

        // تعريف أعمدة جدول المبيعات (Sales)
        const val COL_SALE_ID = "sale_id"
        const val COL_SALE_DATE = "date"
        const val COL_SALE_TOTAL = "total_amount"

        // تعريف أعمدة جدول تفاصيل البيع (Sale_Items)
        const val COL_ITEM_ID = "item_id"
        // COL_SALE_ID و COL_PRODUCT_ID مستخدمان هنا كـ Foreign Keys

        // تعريف أعمدة جدول إعارة الكتب (Book_Loans)
        const val COL_LOAN_ID = "loan_id"
        const val COL_LOAN_BOOK_ID = "book_id" // سيشير إلى product_id
        const val COL_LOAN_BORROWER_NAME = "borrower_name"
        const val COL_LOAN_BORROWER_PHONE = "borrower_phone"
        const val COL_LOAN_BORROW_DATE = "borrow_date"
        const val COL_LOAN_RETURN_EXPECTED = "return_date_expected"
        const val COL_LOAN_RETURN_ACTUAL = "return_date_actual"
        const val COL_LOAN_STATUS = "status"
    }

    // هذه الدالة تُستدعى مرة واحدة فقط عند إنشاء قاعدة البيانات لأول مرة
    override fun onCreate(db: SQLiteDatabase?) {
        // نص برمجي SQL لإنشاء جدول المنتجات
        val createProductsTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COL_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRODUCT_NAME TEXT NOT NULL,
                $COL_PRODUCT_CATEGORY TEXT,
                $COL_PRODUCT_PRICE REAL NOT NULL,
                $COL_PRODUCT_QUANTITY INTEGER NOT NULL,
                $COL_PRODUCT_BARCODE TEXT,
                $COL_PRODUCT_NOTES TEXT
            )
        """

        // نص برمجي SQL لإنشاء جدول المبيعات
        val createSalesTable = """
            CREATE TABLE $TABLE_SALES (
                $COL_SALE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SALE_DATE TEXT NOT NULL,
                $COL_SALE_TOTAL REAL NOT NULL
            )
        """

        // نص برمجي SQL لإنشاء جدول تفاصيل البيع
        val createSaleItemsTable = """
            CREATE TABLE $TABLE_SALE_ITEMS (
                item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SALE_ID INTEGER,
                $COL_PRODUCT_ID INTEGER,
                quantity INTEGER,
                price REAL,
                FOREIGN KEY($COL_SALE_ID) REFERENCES $TABLE_SALES($COL_SALE_ID),
                FOREIGN KEY($COL_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COL_PRODUCT_ID)
            )
        """

        // نص برمجي SQL لإنشاء جدول إعارات الكتب
        val createBookLoansTable = """
            CREATE TABLE $TABLE_BOOK_LOANS (
                $COL_LOAN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_LOAN_BOOK_ID INTEGER,
                $COL_LOAN_BORROWER_NAME TEXT NOT NULL,
                $COL_LOAN_BORROWER_PHONE TEXT,
                $COL_LOAN_BORROW_DATE TEXT NOT NULL,
                $COL_LOAN_RETURN_EXPECTED TEXT NOT NULL,
                $COL_LOAN_RETURN_ACTUAL TEXT,
                $COL_LOAN_STATUS TEXT,
                FOREIGN KEY($COL_LOAN_BOOK_ID) REFERENCES $TABLE_PRODUCTS($COL_PRODUCT_ID)
            )
        """

        // تنفيذ أوامر الإنشاء
        db?.execSQL(createProductsTable)
        db?.execSQL(createSalesTable)
        db?.execSQL(createSaleItemsTable)
        db?.execSQL(createBookLoansTable)
    }

    // هذه الدالة تُستدعى عند تحديث التطبيق وزيادة رقم إصدار قاعدة البيانات (DATABASE_VERSION)
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // سياسة بسيطة للتحديث: حذف الجداول القديمة وإنشاؤها من جديد
        // في التطبيقات الحقيقية، قد تحتاج إلى نقل البيانات القديمة قبل الحذف
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SALE_ITEMS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOK_LOANS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SALES")
        onCreate(db)
    }
}
