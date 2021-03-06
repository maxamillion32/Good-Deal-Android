package data.stevo.SQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emad.gooddeals.tools.ImageToJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stevo on 03/03/16.
 * Class DAO qui va nous permettre de gerer la connexion a notre base de donnée.
 * De gerer les opertions a effectuer sur la table offres de notre base de donnée.
 */
public class OffresDao {
    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_TITRE = 1;
    private static final int NUM_COL_IMAGE = 2;
    private static final int NUM_COL_DESCRIPTION = 3;
    private static final int NUM_COL_CATEGORIE = 4;
    private static final int NUM_COL_MAGASIN = 5;
    private static final int NUM_COL_DATE_FIN = 6;
    private ImageToJson imageToJson = new ImageToJson();
    private JSONObject jsonObject;
    // Champs de la base de données
    private SQLiteDatabase database;
    private GoodDealHelper goodDealHelperHelper;
    private String[] allColumns = {
            GoodDealHelper.COLUMN_ID,
            GoodDealHelper.COLUMN_TITRE,
            GoodDealHelper.COLUMN_IMAGE,
            GoodDealHelper.COLUMN_DESCRIPTIOM,
            GoodDealHelper.COLUMN_CATEGORIE,
            GoodDealHelper.COLUMN_MAGASIN,
            GoodDealHelper.COLUMN_DATE_FIN
    };

    public OffresDao(Context context) {
        //on cree la BDD
        goodDealHelperHelper = new GoodDealHelper(context);
    }

    /**
     * Methode Utilitaire permettant de convertir un string en date
     */
    public static Date convertStringToDate(String dateString) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = df.parse(dateString);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return date;
    }

    /**
     * Methode Utilitaire permettant de convertir une date en String
     */
    public static String convertDateToString(Date date) {
        SimpleDateFormat dateformatJava = new SimpleDateFormat("yyyy-MM-dd");
        String date_to_string = dateformatJava.format(date);

        return date_to_string;
    }

    /**
     * Methode Utilitaire permettant de charger notre date.
     */
    public static Date loadDate(Cursor cursor, int index) {//index ici est lidentifiant de la colonne a laquelle on accede
        Date date;
        date = convertStringToDate(cursor.getString(index));
        if (cursor.isNull(index)) {
            return null;
        }
        return date;
    }

    /**
     * Acces  de notre Base de donnée en lecture
     */
    public void openRead() throws SQLException {
        database = goodDealHelperHelper.getReadableDatabase();
    }

    /**
     * Acces a notre base de donnee en ecriture
     */
    public void openWrite() throws SQLException {
        database = goodDealHelperHelper.getWritableDatabase();
    }

    /**
     * fermeture de notre Base de donnée
     */
    public void close() {
        database.close();
    }

    public SQLiteDatabase getBDD() {
        return database;
    }

    /**
     * Methode permettant de recuperer une offre par son titre
     */
    public Offres getOffreWithTitre(String titre) {
        Cursor cursor = database.query(GoodDealHelper.TABLE_OFFRES,
                allColumns, GoodDealHelper.COLUMN_TITRE + " LIKE \"" + titre + "\"", null,
                null, null, null);
        cursor.moveToFirst();
        //on convertit notre cursor en offre
        Offres offre = cursorToOffre(cursor);
        //on referme le cursor
        cursor.close();
        return offre;
    }

    /**
     * Cette méthode permet de convertir un cursor en une offre
     */
    private Offres cursorToOffre(Cursor cursor) {
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (cursor.getCount() == 0)
            return null;
        //Sinon on se place sur le premier élément

        //On créé une offre
        Offres offre = new Offres();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        offre.setId(cursor.getInt(NUM_COL_ID));
        offre.setTitre(cursor.getString(NUM_COL_TITRE));
        offre.setBipmapImage(cursor.getString(NUM_COL_IMAGE));
        offre.setDescription(cursor.getString(NUM_COL_DESCRIPTION));
        offre.setCategorie(cursor.getString(NUM_COL_CATEGORIE));
        offre.setMagasin(cursor.getString(NUM_COL_MAGASIN));
        offre.setDateFin(loadDate(cursor, NUM_COL_DATE_FIN));
        //On ferme le cursor
        //On retourne l'offre
        return offre;
    }

    /**
     * Cette méthode permet d'inserer une offre en BDD
     */
    public long insertOffre(Offres offre) {
        //Création d'un ContentValues
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        try {
            values.put(GoodDealHelper.COLUMN_ID, offre.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        values.put(GoodDealHelper.COLUMN_TITRE, offre.getTitre());
        values.put(GoodDealHelper.COLUMN_IMAGE, imageToJson.getStringFromBitmap(offre.getBipmapImage()));
        values.put(GoodDealHelper.COLUMN_DESCRIPTIOM, offre.getDescription());
        values.put(GoodDealHelper.COLUMN_CATEGORIE, offre.getCategorie());
        values.put(GoodDealHelper.COLUMN_MAGASIN, offre.getMagasin());
        values.put(GoodDealHelper.COLUMN_DATE_FIN, convertDateToString(offre.getDateFin()));
        //on insère l'objet dans la BDD via le ContentValues
        return database.insert(GoodDealHelper.TABLE_OFFRES, null, values);
    }

    /**
     * Cette méthode permet de modifier une offre en BDD
     */
    public int updateOffre(int id, Offres offre) {
        //il faut simple préciser quelle offre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(GoodDealHelper.COLUMN_TITRE, offre.getTitre());
        values.put(GoodDealHelper.COLUMN_IMAGE, imageToJson.getStringFromBitmap(offre.getBipmapImage()));
        values.put(GoodDealHelper.COLUMN_DESCRIPTIOM, offre.getDescription());
        values.put(GoodDealHelper.COLUMN_CATEGORIE, offre.getCategorie());
        values.put(GoodDealHelper.COLUMN_MAGASIN, offre.getMagasin());
        values.put(GoodDealHelper.COLUMN_DATE_FIN, convertDateToString(offre.getDateFin()));
        return database.update(GoodDealHelper.TABLE_OFFRES, values, GoodDealHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Cette méthode permet de supprimer une offre en BDD
     */
    public int removeOffreWithID(int id) {
        //Suppression d'une offre de la BDD grâce à l'ID
        return database.delete(GoodDealHelper.TABLE_OFFRES, GoodDealHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Cette méthode permet de recuperer toutes offres presentes en BDD
     */
    public ArrayList<Offres> getAllOffres() {
        ArrayList<Offres> offres = new ArrayList<Offres>();

        Cursor cursor = database.query(GoodDealHelper.TABLE_OFFRES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Offres offre = cursorToOffre(cursor);
            offres.add(offre);
            cursor.moveToNext();
        }
        cursor.close();
        return offres;
    }
}

