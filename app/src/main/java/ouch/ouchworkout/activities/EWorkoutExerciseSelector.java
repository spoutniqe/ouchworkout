package ouch.ouchworkout.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import ouch.ouchworkout.Exercise;
import ouch.ouchworkout.Factory;
import ouch.ouchworkout.R;

public class EWorkoutExerciseSelector extends AppCompatActivity {
    private final String darkColor = "#cfecfc";
    private final String lightColor = "#dadae1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_workout_exercise_selector);
        // Get the exercise list container
        final LinearLayout exercise_list = findViewById(R.id.all_exercises);
        exercise_list.removeAllViews();
        // Configure the done button
        Button doneButton = findViewById(R.id.n_w_p_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EWorkoutOrganizer.class);
                startActivity(intent);
            }
        });
        // Configure the filter buttons
        Button strength = findViewById(R.id.strength_filter);
        Button stretch = findViewById(R.id.stretch_filter);
        Button machine = findViewById(R.id.machine_filter);
        Button dumbbell = findViewById(R.id.dumbbell_filter);
        final List<Button> filterButtons = new LinkedList<>();
        filterButtons.add(strength);
        filterButtons.add(stretch);
        filterButtons.add(machine);
        filterButtons.add(dumbbell);
        for (Button filter : filterButtons) {
            filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (Button fb : filterButtons) {
                        fb.getBackground().setColorFilter(Color.parseColor(lightColor),
                                PorterDuff.Mode.MULTIPLY);
                    }
                    view.getBackground().setColorFilter(Color.parseColor(darkColor),
                            PorterDuff.Mode.MULTIPLY);
                    displayExercises(((Button) view).getText().toString(), exercise_list);
                }
            });
        }
        // Set button styles
        for (Button fb : filterButtons) {
            fb.getBackground().setColorFilter(Color.parseColor(lightColor),
                    PorterDuff.Mode.MULTIPLY);
        }
        strength.getBackground().setColorFilter(Color.parseColor(darkColor),
                PorterDuff.Mode.MULTIPLY);
        displayExercises("strength", exercise_list);
    }

    private void displayExercises(String pType, LinearLayout pExerciseList) {
        pExerciseList.removeAllViews();
        for (final Exercise ex : Factory.getInstance().getExercisesFromType(pType)) {
            LinearLayout container = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 0, 10);
            container.setLayoutParams(lp);
            container.setOrientation(LinearLayout.VERTICAL);
            // Add the name and the exercise counter
            LinearLayout nameCounter = new LinearLayout(getApplicationContext());
            TextView exName = new TextView(getApplicationContext());
            exName.setText(ex.getName());
            exName.setTextSize(18);
            LinearLayout.LayoutParams nameParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 2);
            exName.setLayoutParams(nameParams);
            final TextView exCounter = new TextView(getApplicationContext());
            exCounter.setText("#0");
            exCounter.setTextSize(18);
            exCounter.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams counterParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            exCounter.setLayoutParams(counterParams);
            nameCounter.addView(exName);
            nameCounter.addView(exCounter);
            container.addView(nameCounter);
            // Add the image
            ImageView img = new ImageView(getApplicationContext());
            img.setImageResource(getResources().getIdentifier(
                    ex.getPictureName(), "drawable", getPackageName()));
            LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    200);
            imgLp.gravity = Gravity.CENTER;
            img.setLayoutParams(imgLp);
            container.addView(img);
            // Add button
            Button add = new Button(getApplicationContext());
            add.setText("add");
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LinearLayout) view.getParent().getParent()).setBackgroundColor(Color.parseColor(darkColor));
                    Factory.getInstance().getCurrentWorkout().addExercise(ex);
                    int nbEx = Integer.parseInt(
                            exCounter.getText().subSequence(1, exCounter.getText().length()).toString());
                    exCounter.setText("#" + (nbEx + 1));
                }
            });
            // Remove button
            Button remove = new Button(getApplicationContext());
            remove.setText("remove");
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int nbEx = Integer.parseInt(
                            exCounter.getText().subSequence(1, exCounter.getText().length()).toString());
                    if (nbEx > 0) {
                        nbEx--;
                        exCounter.setText("#" + nbEx);
                        Factory.getInstance().getCurrentWorkout().removeExercise(ex);
                        if (nbEx == 0) {
                            ((LinearLayout) view.getParent().getParent()).setBackgroundResource(android.R.color.background_light);
                        }
                    }
                }
            });
            // Container for the add and remove buttons
            LinearLayout buttonContainer = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams bcp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            add.setLayoutParams(bcp);
            remove.setLayoutParams(bcp);
            buttonContainer.setLayoutParams(bcp);
            buttonContainer.addView(remove);
            buttonContainer.addView(add);
            container.addView(buttonContainer);
            pExerciseList.addView(container);
            // Check if the exercise is already in the workout
            int countEx = Factory.getInstance().getCurrentWorkout().countExercise(ex);
            if (countEx > 0) {
                ((LinearLayout) add.getParent().getParent()).setBackgroundColor(Color.parseColor(darkColor));
                exCounter.setText("#" + countEx);
            }
        }
    }
}