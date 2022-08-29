package com.lexst64.lingvolivetelegrambot.database;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvoliveapi.lang.LangType;

import javax.validation.constraints.NotNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {

    private static volatile DBManager instance;
    private static volatile DBConnection connection;

    private DBManager(boolean recreate) {
        connection = new DBConnection();
        if (recreate) {
            recreateDB();
        }
    }

    private void recreateDB() {
        try {
            connection.initTransaction();
            connection.execute("CREATE TABLE Users (user_id INTEGER PRIMARY KEY UNIQUE NOT NULL, src_lang INTEGER NOT NULL, dst_lang INTEGER NOT NULL)");
            connection.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createNewUser(long userId, LangPair langPair) {
        int updatedRows = 0;
        try {
            PreparedStatement statement = connection
                    .getPreparedStatement("INSERT OR REPLACE INTO Users (user_id, src_lang, dst_lang) VALUES(?, ?, ?)");
            statement.setLong(1, userId);
            statement.setInt(2, langPair.getSrcLang().getCode());
            statement.setInt(3, langPair.getDstLang().getCode());
            updatedRows = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    public LangPair getLangPair(long userId) {
        return LangPair.getPair(getSrcLang(userId), getDstLang(userId));
    }

    public boolean updateLangPair(long userId, LangPair langPair) {
        return updateSrcLang(userId, langPair.getSrcLang()) && updateDstLang(userId, langPair.getDstLang());
    }

    private boolean updateSrcLang(long userId, @NotNull Lang srcLang) {
        return updateLang(userId, LangType.SRC_LANG, srcLang.getCode());
    }

    private boolean updateDstLang(long userId, @NotNull Lang dstLang) {
        return updateLang(userId, LangType.DST_LANG, dstLang.getCode());
    }

    private boolean updateLang(long userId, @NotNull LangType langType, int langCode) {
        int updatedRows = 0;
        try {
            PreparedStatement statement;
            if (langType == LangType.SRC_LANG) {
                statement = connection.getPreparedStatement("UPDATE Users SET src_lang = ? WHERE user_id = ?");
            } else {
                statement = connection.getPreparedStatement("UPDATE Users SET dst_lang = ? WHERE user_id = ?");
            }
            statement.setInt(1, langCode);
            statement.setLong(2, userId);
            updatedRows = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    public Lang getSrcLang(long userId) {
        return Lang.getLangByCode(getLangCode(userId, LangType.SRC_LANG));
    }

    public Lang getDstLang(long userId) {
        return Lang.getLangByCode(getLangCode(userId, LangType.DST_LANG));
    }

    private int getLangCode(long userId, LangType langType) {
        int langCode = 0;
        try {
            PreparedStatement statement;
            if (langType == LangType.SRC_LANG) {
                statement = connection.getPreparedStatement("SELECT src_lang FROM Users WHERE user_id = ?");
            } else {
                statement = connection.getPreparedStatement("SELECT dst_lang FROM Users WHERE user_id = ?");
            }
            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                langCode = result.getInt(langType == LangType.SRC_LANG ? "src_lang" : "dst_lang");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return langCode;
    }

    public static DBManager getInstance() {
        DBManager result = instance;
        if (result != null) {
            return instance;
        }
        synchronized (DBManager.class) {
            if (instance == null) {
                instance = new DBManager(false);
            }
            return instance;
        }
    }
}
