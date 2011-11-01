This is a fork of [Satyan's Sugar](https://github.com/satyan/sugar), a wrapper for Android's DB interface for SQLite, fixing some problems with the original library and adding support for `Enum-type` fields.

## Without Sugar

# Table Creation

    final String CREATE_TABLE_COUNTRIES =  "CREATE TABLE tbl_countries ("  + "id INTEGER PRIMARY KEY AUTOINCREMENT,"  + "country_name TEXT);";
    
    final String CREATE_TABLE_STATES =  "CREATE TABLE tbl_states ("  + "id INTEGER PRIMARY KEY AUTOINCREMENT,"  + "state_name TEXT);";
    
    db.execSQL(CREATE_TABLE_COUNTRIES);
    db.execSQL(CREATE_TABLE_STATES);

# Inserting Values

    ContentValues values = new ContentValues();
    values.put("country_name", "India");
    db.insert("tbl_countries", null, values);

## With Sugar

# Table Creation

    public class Country extends SugarRecord<Country> {
      
        String countryName;
        
        public Country(Context context, String countryName) {
          
            super(context);
            this.countryName = countryName;
        }
    }

# Inserting Values

    Country country = new Country(context, "India");
    country.save();

# Query

    Country.findById(context, Country.class, 1);
    Country.find(context, Country.class, "country_name=?", new String[]{"India"});

# Delete

    Country country = Country.findById(context, Country.class, 1);
    country.delete();

# Few More

    Country.listAll(context, Country.class);
    Country.deleteAll(context, Country.class);


Example project:
Note Manager
https://github.com/satyan/SugarExample

More documentation to follow - for now please visit [the Wiki section of the original Sugar project](https://github.com/satyan/sugar/wiki) for more details.