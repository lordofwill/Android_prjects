package com.example.roomdbpractice;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private AppDatabase db;

    public MainViewModel(@NonNull Application application) {
        super(application);
        db = Room.databaseBuilder(application, AppDatabase.class, "todo-db").build();
    }

    LiveData<List<Todo>> getAll() {
        return db.todoDao().getAll();
    }

    void insert(Todo todo) {
        new InsertAsyncTask(db.todoDao()).execute(todo);
    }

    void erase(String toErase) {
        new eraseAsyncTask(db.todoDao()).execute(toErase);
    }//

    void clear() {
        new ClearAsyncTask(db.todoDao()).execute();
    }

    private  static class InsertAsyncTask extends AsyncTask<Todo, Void, Void> {
        private TodoDao mTodoDao;

        InsertAsyncTask(TodoDao todoDao) {
            this.mTodoDao = todoDao;
        }

        @Override
        protected Void doInBackground(Todo... todos) {
            mTodoDao.insert(todos[0]);//넘어온 것 중 하나만 보낸다?
            return null;
        }
    }

    private  static class eraseAsyncTask extends AsyncTask<String, Void, Void> {
        private TodoDao mTodoDao;

        eraseAsyncTask(TodoDao todoDao) {
            this.mTodoDao = todoDao;
        }


        @Override
        protected Void doInBackground(String... strings) {
            mTodoDao.erase(strings[0]);//넘어온 것 중 하나만 보낸다?
            return null;
        }
    }//

    private  static class ClearAsyncTask extends AsyncTask<Todo, Void, Void> {
        private TodoDao mTodoDao;

        ClearAsyncTask(TodoDao todoDao) {
            this.mTodoDao = todoDao;
        }

        @Override
        protected Void doInBackground(Todo... todos) {
            mTodoDao.clear();

            return null;
        }
    }
}
