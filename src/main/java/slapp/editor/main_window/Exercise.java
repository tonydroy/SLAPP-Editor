package slapp.editor.main_window;

import slapp.editor.decorated_rta.DecoratedRTA;

public interface Exercise {

    ExerciseType getType();
    ExerciseModel getExerciseModel();
    ExerciseView getExerciseView();
    ExerciseController getExerciseController();
    void setExerciseModel(ExerciseModel model);
    void setExerciseView(ExerciseView view);
    void setExerciseController(ExerciseController controller);





}
