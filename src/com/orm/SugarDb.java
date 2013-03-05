package com.orm;

import static com.orm.SugarConfig.getDatabaseVersion;
import static com.orm.SugarConfig.getDebugEnabled;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import dalvik.system.DexFile;

public class SugarDb extends SQLiteOpenHelper {
    private Context context;

    public SugarDb(Context context, String pName) {
        super(context, pName, new SugarCursorFactory(getDebugEnabled(context)), getDatabaseVersion(context));
        this.context = context;
    }

    private static <T extends SugarRecord> List<T> getDomainClasses(Context context) {
        List<T> domainClasses = new ArrayList<T>();
        try {
            Enumeration allClasses = getAllClasses(context);

            while (allClasses.hasMoreElements()) {
                String className = (String) allClasses.nextElement();
                T domainClass = getDomainClass(className, context);
                if (domainClass != null) {
                    Log.i("Sugar", "got domain class for " + className);
                    domainClasses.add(domainClass);
                }
            }

        } catch (IOException e) {
            Log.e("Sugar", e.getMessage());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Sugar", e.getMessage());
        }

        return domainClasses;
    }

    private static <T extends SugarRecord> T getDomainClass(String className, Context context) {

        // Log.i("Sugar", "getDomainClass " + className);
        Class discoveredClass = null;
        Class superClass = null;
        try {
            discoveredClass = Class.forName(className, true, context.getClass().getClassLoader());
            superClass = discoveredClass.getSuperclass();
        } catch (ClassNotFoundException e) {
            Log.e("Sugar", e.getMessage());
        }

        if ((discoveredClass == null) || (superClass == null)
                || (!discoveredClass.getSuperclass().equals(SugarRecord.class))) {
            return null;
        } else {
            try {
                return (T) discoveredClass.getDeclaredConstructor(Context.class).newInstance(context);
            } catch (InstantiationException e) {
                Log.e("Sugar", "couldn't process domain class " + className, e);
            } catch (IllegalAccessException e) {
                Log.e("Sugar", "couldn't process domain class " + className, e);
            } catch (NoSuchMethodException e) {
                Log.e("Sugar", "couldn't process domain class " + className, e);
            } catch (InvocationTargetException e) {
                Log.e("Sugar", "couldn't process domain class " + className, e);
            }
        }

        return null;

    }

    private static Enumeration getAllClasses(Context context) throws PackageManager.NameNotFoundException, IOException {
        String path = getSourcePath(context);
        DexFile dexfile = new DexFile(path);
        return dexfile.entries();
    }

    private static String getSourcePath(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("Sugar", "on create");
        createDatabase(sqLiteDatabase);
    }

    private <T extends SugarRecord> void createDatabase(SQLiteDatabase sqLiteDatabase) {
        Log.i("Sugar", "createDatabase");
        List<T> domainClasses = getDomainClasses(context);
        for (T domain : domainClasses) {

            createTable(domain, sqLiteDatabase);
        }
    }

    private <T extends SugarRecord> void createTable(T table, SQLiteDatabase sqLiteDatabase) {
        Log.i("Sugar", "create table");
        List<Field> fields = table.getTableFields();
        StringBuilder sb = new StringBuilder("CREATE TABLE ").append(table.getSqlName()).append(
                " ( ID INTEGER PRIMARY KEY AUTOINCREMENT ");

        for (Field column : fields) {
            String columnName = StringUtil.toSQLName(column.getName());
            String columnType = QueryBuilder.getColumnType(column.getType());

            if (columnType != null) {

                if (columnName.equalsIgnoreCase("Id")) {
                    continue;
                }
                sb.append(", ").append(columnName).append(" ").append(columnType);
            }
        }
        sb.append(" ) ");

        Log.i("Sugar", "creating table " + table.getSqlName() + ": " + sb.toString());

        if (!"".equals(sb.toString())) {
            sqLiteDatabase.execSQL(sb.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        Log.v("Sugar", "onUpgrade");

        deleteTables(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    private <T extends SugarRecord> void deleteTables(SQLiteDatabase sqLiteDatabase) {
        List<T> tables = getDomainClasses(this.context);
        for (T table : tables) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table.getSqlName());
        }
    }
}
