package net.ivanvega.basededatoslocalconrooma.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Query("select * from User")
    public List<User> getAll();

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);

    @Query("SELECT * FROM user WHERE uid LIKE :id LIMIT 1")
    User selectUser(int id);

    @Query("SELECT * FROM user WHERE first_name LIKE :first")
    List<User> listNombres(String first);

    @Insert
    void insertAll(User... users);

    @Insert
    long insert(User user);

    @Delete
    void delete(User user);

    @Update
    int updateUser(User user);

    @Delete
    int deleteUsuario(User user);
}
