import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//Regular class (1.0) only up to 6 sections
//Half credit courses (0.5) up to 6 sections
//Lab classes, (1.5) only up to 6
//Will check up to 11 classes per student
//Should try to add how many arrangements are possible for each placement
//Make it so we can print a kid schedule to show conflicts
//should be able to enter a kid into a method, get conflicts, with option to add a course to check
//how does it handle if we double up a section - should we add it twice, think about seat counts
//inputting false on the check method means that the lab doesn't have to be adjacent to the class
/* use x.check() for total conflicts
 * x.sched(Student x) gives the student schedule, along with any conflict groups.
 *
 *
 * Results for 2019-2020 after Scheduling Committee:
 * Total Students: 471
Number of students fully scheduled:386
Number of students Down 1 Course:83
Number of students Down 2 Courses:2
Number of students Down 3 Courses:0
Number of students Down 4 Courses:0
 *
 *
Made a Guidance Method for printing conflicts by student, alphabetically
 */
class Master {
    ArrayList<Course> master = new ArrayList<>();
    ArrayList<Course> courses = new ArrayList<>();
    ArrayList<Student> students = new ArrayList<>();
    ArrayList<ArrayList<Course>> conflicts = new ArrayList<>();
    ArrayList<ArrayList<Student>> conflictedStudents = new ArrayList<>();
    ArrayList<Course> conflictedCourses = new ArrayList<>();

    Master() {

    }

    public void sched(Student x) {
        System.out.println("Schedule for " + x.name);
        System.out.println("Number of Courses Scheduled: " + x.courses.size());
        ArrayList<ArrayList<ArrayList<Integer>>> temp = new ArrayList<>();
        for (int i = 0; i < x.courses.size(); i++)
            temp.add(x.courses.get(i).periods);
        int able = take3(temp);
        System.out.println("Number of Courses Schedulable: " + able);

        System.out.println("\t\t\t\t" + "1A" + "\t" + "1B" + "\t" + "2A" + "\t" + "2B" + "\t" + "3A" + "\t" + "3B"
                + "\t" + "4A" + "\t" + "4B" + "\t" + "5A" + "\t" + "5B" + "\t" + "6A" + "\t" + "6B" + "\t" + "7A" + "\t"
                + "7B" + "\t" + "8A" + "\t" + "8B");
        String[][] sched = new String[x.courses.size()][16];
        for (int i = 0; i < x.courses.size(); i++)// for each course
        {
            for (int j = 0; j < x.courses.get(i).periods.size(); j++)// for each section
            {

                for (int k = 0; k < x.courses.get(i).periods.get(j).size(); k++)// for each period
                {
                    if (x.courses.get(i).numSecs == 1)
                        sched[i][x.courses.get(i).periods.get(j).get(k) - 1] = "S";
                    else if (sched[i][x.courses.get(i).periods.get(j).get(k) - 1] == null)
                        sched[i][x.courses.get(i).periods.get(j).get(k) - 1] = "" + (char) (65 + j);
                    else
                        sched[i][x.courses.get(i).periods.get(j).get(k) - 1] += "" + (char) (65 + j);
                }
            }

        }
        for (int i = 0; i < sched.length; i++)// rows
        {
            int len = x.courses.get(i).name.length() / 8;

            System.out.print(x.courses.get(i).name);
            for (int t = len; t < 4; t++)
                System.out.print("\t");
            for (int j = 0; j < sched[0].length; j++)// cols
            {
                if (sched[i][j] != null)
                    System.out.print(sched[i][j] + "\t");
                else
                    System.out.print("\t");
            }
            System.out.println();
            System.out.println();
        }

        check(x);
        System.out.println(x.stuConflicts);

    }

    public void guidance() {
        students.sort(null);
        int count = 1;
        for (int i = 0; i < students.size(); i++) {
            check(students.get(i));
            if (students.get(i).stuConflicts.size() > 0) {
                System.out.println(count + " - " + students.get(i).name + " - " + students.get(i).stuConflicts);
                count++;
            }
        }

    }

    public void check() {
        for (int i = 0; i < students.size(); i++) {
            check(students.get(i));

        }

        for (int i = 0; i < students.size(); i++) {
            for (int j = 0; j < students.get(i).stuConflicts.size(); j++) {
                if (!(conflicts.contains(students.get(i).stuConflicts.get(j))))
                    conflicts.add(students.get(i).stuConflicts.get(j));
            }
        }
        ArrayList<Course> conflictedCourses = new ArrayList<>();
        ArrayList<Integer> numConflictedGroup = new ArrayList<>();
        ArrayList<ArrayList<Student>> conflictedStu = new ArrayList<>();
        ArrayList<Student> downTwos = new ArrayList<>();

        for (int i = 0; i < conflicts.size(); i++) {
            for (int j = 0; j < conflicts.get(i).size(); j++) {
                conflicts.get(i).get(j).numConflictGroups++;
                if (!(conflictedCourses.contains(conflicts.get(i).get(j))))
                    conflictedCourses.add(conflicts.get(i).get(j));
            }
        }

        Course temp = new Course();

        for (int j = 0; j < conflictedCourses.size(); j++) {
            Course max = conflictedCourses.get(j);
            int maxSpot = j;
            for (int i = j + 1; i < conflictedCourses.size(); i++) {
                if (conflictedCourses.get(i).numConflictGroups > max.numConflictGroups) {
                    max = conflictedCourses.get(i);
                    maxSpot = i;
                }
            }
            temp = max;
            conflictedCourses.set(maxSpot, conflictedCourses.get(j));
            conflictedCourses.set(j, temp);

        }
        System.out.println("Number of Conflict Groups:");
        for (int i = 0; i < conflictedCourses.size(); i++) {
            System.out.println(conflictedCourses.get(i).name + " " + conflictedCourses.get(i).numConflictGroups);
        }

        for (int i = 0; i < conflictedCourses.size(); i++) {
            for (int j = 0; j < students.size(); j++) {
                for (int k = 0; k < students.get(j).stuConflicts.size(); k++) {
                    if ((students.get(j).stuConflicts.get(k).contains(conflictedCourses.get(i))
                            && !(conflictedCourses.get(i).conflictedStudents.contains(students.get(j))))) {
                        conflictedCourses.get(i).conflictedStudents.add(students.get(j));
                        conflictedCourses.get(i).numStudentsConflicted++;
                    }
                }
            }

        }

        for (int j = 0; j < conflictedCourses.size(); j++) {
            Course max = conflictedCourses.get(j);
            int maxSpot = j;
            for (int i = j + 1; i < conflictedCourses.size(); i++) {
                if (conflictedCourses.get(i).numStudentsConflicted > max.numStudentsConflicted) {
                    max = conflictedCourses.get(i);
                    maxSpot = i;
                }
            }
            temp = max;
            conflictedCourses.set(maxSpot, conflictedCourses.get(j));
            conflictedCourses.set(j, temp);

        }

        System.out.println();
        System.out.println();

        System.out.println("Number of Students Conflicted for Each Course:");
        System.out.println();

        for (int i = 0; i < conflictedCourses.size(); i++) {
            System.out.println(conflictedCourses.get(i).name + " - " + conflictedCourses.get(i).numStudentsConflicted
                    + " - " + conflictedCourses.get(i).conflictedStudents);
        }

        for (int i = 0; i < conflicts.size(); i++) {
            ArrayList<Student> conflictedStu1 = new ArrayList<>();
            int w = 0;
            for (int j = 0; j < students.size(); j++) {
                if (students.get(j).stuConflicts.contains(conflicts.get(i))) {
                    conflictedStu1.add(students.get(j));
                    w++;
                }

            }
            numConflictedGroup.add(w);
            conflictedStu.add(conflictedStu1);

        }
        int full = 0;
        int lessOne = 0;
        int lessTwo = 0;
        int lessThree = 0;
        int lessFour = 0;

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).dif == 0)
                full++;
            else if (students.get(i).dif == 1)
                lessOne++;
            else if (students.get(i).dif == 2) {
                lessTwo++;
                downTwos.add(students.get(i));
            } else if (students.get(i).dif == 3)
                lessTwo++;
            else if (students.get(i).dif == 4)
                lessFour++;
        }

        System.out.println("Total Students: " + students.size());
        System.out.println("Number of students fully scheduled:" + full);
        System.out.println("Number of students Down 1 Course:" + lessOne);
        System.out.println("Number of students Down 2 Courses:" + lessTwo + " - " + downTwos);
        System.out.println("Number of students Down 3 Courses:" + lessThree);
        System.out.println("Number of students Down 4 Courses:" + lessFour);
        for (int i = 0; i < conflicts.size(); i++)
            System.out.println(numConflictedGroup.get(i) + " students conflicted - " + conflicts.get(i) + " - "
                    + conflictedStu.get(i));

    }

    public void seatCount(int x) {
        double count = 0;
        double countEach = 0;
        String[] periods = {"1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B", "5A", "5B", "6A", "6B", "7A", "7B", "8A",
                "8B"};
        double[] counts = new double[16];
        int yes = 0;

        for (Course course : master) {
            for (int p = 0; p < 16; p++) {
                boolean checkedI = false;
                for (int f = 0; f < course.seatedPeriods.size(); f++) {// for each section
                    if (course.seatedPeriods.get(f).contains(p + 1))//
                        yes++;// yes is the number of sections that have that period (p), usually 0 or 1,
                    // could be more
                }
                if (yes > 0) {
                    for (int j = 0; j < course.roster.size(); j++) {// for each kid in course i
                        if (course.roster.get(j).grade == x) {
                            countEach++;
                        }
                    }
                    if (course.number.charAt(course.number.length() - 1) == 'I'
                            || course.number.charAt(course.number.length() - 1) == 'i') { // if class is inclusive
                        for (Course checkCourse : master) { // searching for matching class, need to search in case not
                            // added in order
                            if (course.name.substring(0, course.name.length() - 1).equals(checkCourse.name)) { // if the
                                // name
                                // of
                                // inclusive
                                // class
                                // not
                                // including
                                // "I"
                                // ==
                                // name
                                // of
                                // found
                                // class
                                for (int ab = 0; ab < checkCourse.seatedPeriods.size(); ab++) { // for each "period"
                                    // stored in class
                                    for (Integer per : checkCourse.seatedPeriods.get(ab)) // for each period in
                                        // ArrayList
                                        counts[per - 1] += yes * countEach / checkCourse.numSecs; // add number of
                                    // distributed
                                    // student to all
                                    // periods in
                                    // regular class
                                }
                                break;
                            }
                        }
                        checkedI = true;
                    } else
                        count += yes * countEach / course.numSecs;
                }

                yes = 0;
                countEach = 0;
                counts[p] += count; // update to Math.round when finished testing
                count = 0;
                if (checkedI)
                    break;
            }
        }
        System.out.println("Seat Count for " + x + "th graders:");
        System.out.println();
        for (int i = 0; i < 16; i++)
            System.out.println("Period " + periods[i] + ": " + counts[i]);
    }

    public void add(Course x, Integer... y) {
        if (!master.contains(x))
            master.add(x);
        if (!x.periods.contains(y)) {
            x.periods.add(new ArrayList<>(Arrays.asList(y)));
            x.posted = true;
            for (int i = 0; i < x.roster.size(); i++) {
                if (!x.roster.get(i).courses.contains(x))
                    x.roster.get(i).courses.add(x);
            }

            for (int i = 0; i < x.roster.size(); i++) {
                if (!students.contains(x.roster.get(i)))
                    students.add(x.roster.get(i));
            }
        }
        x.seatAdd(y);
    }

    public boolean doesntContain(ArrayList<Integer> temp, ArrayList<Integer> tempA) {

        boolean doesntContain = true;
        for (int i = 0; i < tempA.size(); i++) {
            doesntContain = doesntContain && !temp.contains(tempA.get(i));
        }
        return doesntContain;
    }

    public static boolean containsAll(ArrayList<ArrayList<Integer>> y, ArrayList<ArrayList<Integer>> x) {
        for (int i = 0; i < y.size(); i++) {
            boolean match = false;
            for (int j = 0; j < x.size(); j++) {
                if (y.get(i).containsAll(x.get(j))) {
                    match = true;
                    break;
                }

            }
            if (match == false)
                return false;
        }
        return true;
    }

    public int take3(ArrayList<ArrayList<ArrayList<Integer>>> y) {
        if (y.size() == 0)
            return 0;
        if (y.size() == 1) {
            Student.maxP = y.get(0).size();
            return 1;
        }
        ArrayList<ArrayList<Integer>> tempAA = new ArrayList<>();
        ArrayList<Integer> tempA = new ArrayList<>();
        int max = 0;
        if (y.size() == 2) {
            // Student.maxP = 0;
            for (int a = 0; a < y.get(0).size(); a++)// a = 0, 1
            {
                for (int b = 0; b < y.get(1).size(); b++)// b = 0
                {
                    if (doesntContain(tempA, y.get(0).get(a))) {
                        tempAA.add(y.get(0).get(a));
                        tempA.addAll(y.get(0).get(a));
                    }
                    if (doesntContain(tempA, y.get(1).get(b))) {
                        tempAA.add(y.get(1).get(b));
                        tempA.addAll(y.get(1).get(b));
                    }
                    int currentMax = max;
                    max = Math.max(max, tempAA.size());
                    if (currentMax == tempAA.size())
                        Student.maxP++;
                    else if (tempAA.size() > currentMax)// this has to be checked, not sure yet
                    {
                        Student.maxP = 1;
                    }

                    tempAA.clear();
                    tempA.clear();
                }

            }

        }
        if (y.size() == 3) {
            for (int a = 0; a < y.get(0).size(); a++) {
                for (int b = 0; b < y.get(1).size(); b++) {
                    for (int c = 0; c < y.get(2).size(); c++) {
                        if (doesntContain(tempA, y.get(0).get(a))) {
                            tempAA.add(y.get(0).get(a));
                            tempA.addAll(y.get(0).get(a));
                        }
                        if (doesntContain(tempA, y.get(1).get(b))) {
                            tempAA.add(y.get(1).get(b));
                            tempA.addAll(y.get(1).get(b));
                        }
                        if (doesntContain(tempA, y.get(2).get(c))) {
                            tempAA.add(y.get(2).get(c));
                            tempA.addAll(y.get(2).get(c));
                        }
                        int currentMax = max;
                        max = Math.max(max, tempAA.size());
                        if (currentMax == tempAA.size())
                            Student.maxP++;
                        else if (tempAA.size() > currentMax)// this has to be checked, not sure yet
                        {
                            Student.maxP = 1;
                        }

                        tempAA.clear();
                        tempA.clear();
                    }

                }

            }

        }
        if (y.size() == 4) {
            for (int a = 0; a < y.get(0).size(); a++) {
                for (int b = 0; b < y.get(1).size(); b++) {
                    for (int c = 0; c < y.get(2).size(); c++) {
                        for (int d = 0; d < y.get(3).size(); d++) {
                            if (doesntContain(tempA, y.get(0).get(a))) {
                                tempAA.add(y.get(0).get(a));
                                tempA.addAll(y.get(0).get(a));
                            }
                            if (doesntContain(tempA, y.get(1).get(b))) {
                                tempAA.add(y.get(1).get(b));
                                tempA.addAll(y.get(1).get(b));
                            }
                            if (doesntContain(tempA, y.get(2).get(c))) {
                                tempAA.add(y.get(2).get(c));
                                tempA.addAll(y.get(2).get(c));
                            }
                            if (doesntContain(tempA, y.get(3).get(d))) {
                                tempAA.add(y.get(3).get(d));
                                tempA.addAll(y.get(3).get(d));
                            }
                            int currentMax = max;
                            max = Math.max(max, tempAA.size());
                            if (currentMax == tempAA.size())
                                Student.maxP++;
                            else if (tempAA.size() > currentMax)// this has to be checked, not sure yet
                            {
                                Student.maxP = 1;
                            }

                            tempAA.clear();
                            tempA.clear();
                        }
                    }
                }
            }
        }
        if (y.size() == 5) {
            for (int a = 0; a < y.get(0).size(); a++) {
                for (int b = 0; b < y.get(1).size(); b++) {
                    for (int c = 0; c < y.get(2).size(); c++) {
                        for (int d = 0; d < y.get(3).size(); d++) {
                            for (int e = 0; e < y.get(4).size(); e++) {
                                if (doesntContain(tempA, y.get(0).get(a))) {
                                    tempAA.add(y.get(0).get(a));
                                    tempA.addAll(y.get(0).get(a));
                                }
                                if (doesntContain(tempA, y.get(1).get(b))) {
                                    tempAA.add(y.get(1).get(b));
                                    tempA.addAll(y.get(1).get(b));
                                }
                                if (doesntContain(tempA, y.get(2).get(c))) {
                                    tempAA.add(y.get(2).get(c));
                                    tempA.addAll(y.get(2).get(c));
                                }
                                if (doesntContain(tempA, y.get(3).get(d))) {
                                    tempAA.add(y.get(3).get(d));
                                    tempA.addAll(y.get(3).get(d));
                                }
                                if (doesntContain(tempA, y.get(4).get(e))) {
                                    tempAA.add(y.get(4).get(e));
                                    tempA.addAll(y.get(4).get(e));
                                }
                                int currentMax = max;
                                max = Math.max(max, tempAA.size());
                                if (currentMax == tempAA.size())
                                    Student.maxP++;
                                else if (tempAA.size() > currentMax)// this has to be checked, not sure yet
                                {
                                    Student.maxP = 1;
                                }
                                tempAA.clear();
                                tempA.clear();
                            }
                        }
                    }
                }
            }
        }
        if (y.size() == 6) {
            for (int a = 0; a < y.get(0).size(); a++) {
                for (int b = 0; b < y.get(1).size(); b++) {
                    for (int c = 0; c < y.get(2).size(); c++) {
                        for (int d = 0; d < y.get(3).size(); d++) {
                            for (int e = 0; e < y.get(4).size(); e++) {
                                for (int f = 0; f < y.get(5).size(); f++) {
                                    if (doesntContain(tempA, y.get(0).get(a))) {
                                        tempAA.add(y.get(0).get(a));
                                        tempA.addAll(y.get(0).get(a));
                                    }
                                    if (doesntContain(tempA, y.get(1).get(b))) {
                                        tempAA.add(y.get(1).get(b));
                                        tempA.addAll(y.get(1).get(b));
                                    }
                                    if (doesntContain(tempA, y.get(2).get(c))) {
                                        tempAA.add(y.get(2).get(c));
                                        tempA.addAll(y.get(2).get(c));
                                    }
                                    if (doesntContain(tempA, y.get(3).get(d))) {
                                        tempAA.add(y.get(3).get(d));
                                        tempA.addAll(y.get(3).get(d));
                                    }
                                    if (doesntContain(tempA, y.get(4).get(e))) {
                                        tempAA.add(y.get(4).get(e));
                                        tempA.addAll(y.get(4).get(e));
                                    }
                                    if (doesntContain(tempA, y.get(5).get(f))) {
                                        tempAA.add(y.get(5).get(f));
                                        tempA.addAll(y.get(5).get(f));
                                    }
                                    int currentMax = max;
                                    max = Math.max(max, tempAA.size());
                                    if (currentMax == tempAA.size())
                                        Student.maxP++;
                                    else if (tempAA.size() > currentMax)// this has to be checked, not sure yet
                                    {
                                        Student.maxP = 1;
                                    }
                                    tempAA.clear();
                                    tempA.clear();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (y.size() == 7) {
            for (int a = 0; a < y.get(0).size(); a++) {
                for (int b = 0; b < y.get(1).size(); b++) {
                    for (int c = 0; c < y.get(2).size(); c++) {
                        for (int d = 0; d < y.get(3).size(); d++) {
                            for (int e = 0; e < y.get(4).size(); e++) {
                                for (int f = 0; f < y.get(5).size(); f++) {
                                    for (int g = 0; g < y.get(6).size(); g++) {
                                        if (doesntContain(tempA, y.get(0).get(a))) {
                                            tempAA.add(y.get(0).get(a));
                                            tempA.addAll(y.get(0).get(a));
                                        }
                                        if (doesntContain(tempA, y.get(1).get(b))) {
                                            tempAA.add(y.get(1).get(b));
                                            tempA.addAll(y.get(1).get(b));
                                        }
                                        if (doesntContain(tempA, y.get(2).get(c))) {
                                            tempAA.add(y.get(2).get(c));
                                            tempA.addAll(y.get(2).get(c));
                                        }
                                        if (doesntContain(tempA, y.get(3).get(d))) {
                                            tempAA.add(y.get(3).get(d));
                                            tempA.addAll(y.get(3).get(d));
                                        }
                                        if (doesntContain(tempA, y.get(4).get(e))) {
                                            tempAA.add(y.get(4).get(e));
                                            tempA.addAll(y.get(4).get(e));
                                        }
                                        if (doesntContain(tempA, y.get(5).get(f))) {
                                            tempAA.add(y.get(5).get(f));
                                            tempA.addAll(y.get(5).get(f));
                                        }
                                        if (doesntContain(tempA, y.get(6).get(g))) {
                                            tempAA.add(y.get(6).get(g));
                                            tempA.addAll(y.get(6).get(g));
                                        }
                                        int currentMax = max;
                                        max = Math.max(max, tempAA.size());
                                        if (currentMax == tempAA.size())
                                            Student.maxP++;
                                        else if (tempAA.size() > currentMax)// this has to be checked, not sure yet
                                        {
                                            Student.maxP = 1;
                                        }
                                        tempAA.clear();
                                        tempA.clear();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (y.size() == 8) {
            for (int a = 0; a < y.get(0).size(); a++) {
                for (int b = 0; b < y.get(1).size(); b++) {
                    for (int c = 0; c < y.get(2).size(); c++) {
                        for (int d = 0; d < y.get(3).size(); d++) {
                            for (int e = 0; e < y.get(4).size(); e++) {
                                for (int f = 0; f < y.get(5).size(); f++) {
                                    for (int g = 0; g < y.get(6).size(); g++) {
                                        for (int h = 0; h < y.get(7).size(); h++) {
                                            if (doesntContain(tempA, y.get(0).get(a))) {
                                                tempAA.add(y.get(0).get(a));
                                                tempA.addAll(y.get(0).get(a));
                                            }
                                            if (doesntContain(tempA, y.get(1).get(b))) {
                                                tempAA.add(y.get(1).get(b));
                                                tempA.addAll(y.get(1).get(b));
                                            }
                                            if (doesntContain(tempA, y.get(2).get(c))) {
                                                tempAA.add(y.get(2).get(c));
                                                tempA.addAll(y.get(2).get(c));
                                            }
                                            if (doesntContain(tempA, y.get(3).get(d))) {
                                                tempAA.add(y.get(3).get(d));
                                                tempA.addAll(y.get(3).get(d));
                                            }
                                            if (doesntContain(tempA, y.get(4).get(e))) {
                                                tempAA.add(y.get(4).get(e));
                                                tempA.addAll(y.get(4).get(e));
                                            }
                                            if (doesntContain(tempA, y.get(5).get(f))) {
                                                tempAA.add(y.get(5).get(f));
                                                tempA.addAll(y.get(5).get(f));
                                            }
                                            if (doesntContain(tempA, y.get(6).get(g))) {
                                                tempAA.add(y.get(6).get(g));
                                                tempA.addAll(y.get(6).get(g));
                                            }
                                            if (doesntContain(tempA, y.get(7).get(h))) {
                                                tempAA.add(y.get(7).get(h));
                                                tempA.addAll(y.get(7).get(h));
                                            }

                                            int currentMax = max;
                                            max = Math.max(max, tempAA.size());
                                            if (currentMax == tempAA.size())
                                                Student.maxP++;
                                            else if (tempAA.size() > currentMax)// this has to be checked, not sure yet
                                            {
                                                Student.maxP = 1;
                                            }
                                            tempAA.clear();
                                            tempA.clear();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (y.size() == 9) {
            for (int a = 0; a < y.get(0).size(); a++) {
                for (int b = 0; b < y.get(1).size(); b++) {
                    for (int c = 0; c < y.get(2).size(); c++) {
                        for (int d = 0; d < y.get(3).size(); d++) {
                            for (int e = 0; e < y.get(4).size(); e++) {
                                for (int f = 0; f < y.get(5).size(); f++) {
                                    for (int g = 0; g < y.get(6).size(); g++) {
                                        for (int h = 0; h < y.get(7).size(); h++) {
                                            for (int i = 0; i < y.get(8).size(); i++) {
                                                if (doesntContain(tempA, y.get(0).get(a))) {
                                                    tempAA.add(y.get(0).get(a));
                                                    tempA.addAll(y.get(0).get(a));
                                                }
                                                if (doesntContain(tempA, y.get(1).get(b))) {
                                                    tempAA.add(y.get(1).get(b));
                                                    tempA.addAll(y.get(1).get(b));
                                                }
                                                if (doesntContain(tempA, y.get(2).get(c))) {
                                                    tempAA.add(y.get(2).get(c));
                                                    tempA.addAll(y.get(2).get(c));
                                                }
                                                if (doesntContain(tempA, y.get(3).get(d))) {
                                                    tempAA.add(y.get(3).get(d));
                                                    tempA.addAll(y.get(3).get(d));
                                                }
                                                if (doesntContain(tempA, y.get(4).get(e))) {
                                                    tempAA.add(y.get(4).get(e));
                                                    tempA.addAll(y.get(4).get(e));
                                                }
                                                if (doesntContain(tempA, y.get(5).get(f))) {
                                                    tempAA.add(y.get(5).get(f));
                                                    tempA.addAll(y.get(5).get(f));
                                                }
                                                if (doesntContain(tempA, y.get(6).get(g))) {
                                                    tempAA.add(y.get(6).get(g));
                                                    tempA.addAll(y.get(6).get(g));
                                                }
                                                if (doesntContain(tempA, y.get(7).get(h))) {
                                                    tempAA.add(y.get(7).get(h));
                                                    tempA.addAll(y.get(7).get(h));
                                                }
                                                if (doesntContain(tempA, y.get(8).get(i))) {
                                                    tempAA.add(y.get(8).get(i));
                                                    tempA.addAll(y.get(8).get(i));
                                                }

                                                int currentMax = max;
                                                max = Math.max(max, tempAA.size());
                                                if (currentMax == tempAA.size())
                                                    Student.maxP++;
                                                else if (tempAA.size() > currentMax)// this has to be checked, not sure
                                                // yet
                                                {
                                                    Student.maxP = 1;
                                                }
                                                tempAA.clear();
                                                tempA.clear();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (y.size() == 10) {
            for (int a = 0; a < y.get(0).size(); a++) {
                for (int b = 0; b < y.get(1).size(); b++) {
                    for (int c = 0; c < y.get(2).size(); c++) {
                        for (int d = 0; d < y.get(3).size(); d++) {
                            for (int e = 0; e < y.get(4).size(); e++) {
                                for (int f = 0; f < y.get(5).size(); f++) {
                                    for (int g = 0; g < y.get(6).size(); g++) {
                                        for (int h = 0; h < y.get(7).size(); h++) {
                                            for (int i = 0; i < y.get(8).size(); i++) {
                                                for (int j = 0; j < y.get(9).size(); j++) {
                                                    if (doesntContain(tempA, y.get(0).get(a))) {
                                                        tempAA.add(y.get(0).get(a));
                                                        tempA.addAll(y.get(0).get(a));
                                                    }
                                                    if (doesntContain(tempA, y.get(1).get(b))) {
                                                        tempAA.add(y.get(1).get(b));
                                                        tempA.addAll(y.get(1).get(b));
                                                    }
                                                    if (doesntContain(tempA, y.get(2).get(c))) {
                                                        tempAA.add(y.get(2).get(c));
                                                        tempA.addAll(y.get(2).get(c));
                                                    }
                                                    if (doesntContain(tempA, y.get(3).get(d))) {
                                                        tempAA.add(y.get(3).get(d));
                                                        tempA.addAll(y.get(3).get(d));
                                                    }
                                                    if (doesntContain(tempA, y.get(4).get(e))) {
                                                        tempAA.add(y.get(4).get(e));
                                                        tempA.addAll(y.get(4).get(e));
                                                    }
                                                    if (doesntContain(tempA, y.get(5).get(f))) {
                                                        tempAA.add(y.get(5).get(f));
                                                        tempA.addAll(y.get(5).get(f));
                                                    }
                                                    if (doesntContain(tempA, y.get(6).get(g))) {
                                                        tempAA.add(y.get(6).get(g));
                                                        tempA.addAll(y.get(6).get(g));
                                                    }
                                                    if (doesntContain(tempA, y.get(7).get(h))) {
                                                        tempAA.add(y.get(7).get(h));
                                                        tempA.addAll(y.get(7).get(h));
                                                    }
                                                    if (doesntContain(tempA, y.get(8).get(i))) {
                                                        tempAA.add(y.get(8).get(i));
                                                        tempA.addAll(y.get(8).get(i));
                                                    }
                                                    if (doesntContain(tempA, y.get(9).get(j))) {
                                                        tempAA.add(y.get(9).get(j));
                                                        tempA.addAll(y.get(9).get(j));
                                                    }
                                                    int currentMax = max;
                                                    max = Math.max(max, tempAA.size());
                                                    if (currentMax == tempAA.size())
                                                        Student.maxP++;
                                                    else if (tempAA.size() > currentMax)// this has to be checked, not
                                                    // sure yet
                                                    {
                                                        Student.maxP = 1;
                                                    }
                                                    tempAA.clear();
                                                    tempA.clear();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (y.size() == 11) {
            for (int a = 0; a < y.get(0).size(); a++) {
                for (int b = 0; b < y.get(1).size(); b++) {
                    for (int c = 0; c < y.get(2).size(); c++) {
                        for (int d = 0; d < y.get(3).size(); d++) {
                            for (int e = 0; e < y.get(4).size(); e++) {
                                for (int f = 0; f < y.get(5).size(); f++) {
                                    for (int g = 0; g < y.get(6).size(); g++) {
                                        for (int h = 0; h < y.get(7).size(); h++) {
                                            for (int i = 0; i < y.get(8).size(); i++) {
                                                for (int j = 0; j < y.get(9).size(); j++) {
                                                    for (int k = 0; k < y.get(10).size(); k++) {
                                                        if (doesntContain(tempA, y.get(0).get(a))) {
                                                            tempAA.add(y.get(0).get(a));
                                                            tempA.addAll(y.get(0).get(a));
                                                        }
                                                        if (doesntContain(tempA, y.get(1).get(b))) {
                                                            tempAA.add(y.get(1).get(b));
                                                            tempA.addAll(y.get(1).get(b));
                                                        }
                                                        if (doesntContain(tempA, y.get(2).get(c))) {
                                                            tempAA.add(y.get(2).get(c));
                                                            tempA.addAll(y.get(2).get(c));
                                                        }
                                                        if (doesntContain(tempA, y.get(3).get(d))) {
                                                            tempAA.add(y.get(3).get(d));
                                                            tempA.addAll(y.get(3).get(d));
                                                        }
                                                        if (doesntContain(tempA, y.get(4).get(e))) {
                                                            tempAA.add(y.get(4).get(e));
                                                            tempA.addAll(y.get(4).get(e));
                                                        }
                                                        if (doesntContain(tempA, y.get(5).get(f))) {
                                                            tempAA.add(y.get(5).get(f));
                                                            tempA.addAll(y.get(5).get(f));
                                                        }
                                                        if (doesntContain(tempA, y.get(6).get(g))) {
                                                            tempAA.add(y.get(6).get(g));
                                                            tempA.addAll(y.get(6).get(g));
                                                        }
                                                        if (doesntContain(tempA, y.get(7).get(h))) {
                                                            tempAA.add(y.get(7).get(h));
                                                            tempA.addAll(y.get(7).get(h));
                                                        }
                                                        if (doesntContain(tempA, y.get(8).get(i))) {
                                                            tempAA.add(y.get(8).get(i));
                                                            tempA.addAll(y.get(8).get(i));
                                                        }
                                                        if (doesntContain(tempA, y.get(9).get(j))) {
                                                            tempAA.add(y.get(9).get(j));
                                                            tempA.addAll(y.get(9).get(j));
                                                        }
                                                        if (doesntContain(tempA, y.get(10).get(k))) {
                                                            tempAA.add(y.get(10).get(k));
                                                            tempA.addAll(y.get(10).get(k));
                                                        }

                                                        int currentMax = max;
                                                        max = Math.max(max, tempAA.size());
                                                        if (currentMax == tempAA.size())
                                                            Student.maxP++;
                                                        else if (tempAA.size() > currentMax)// this has to be checked,
                                                        // not sure yet
                                                        {
                                                            Student.maxP = 1;
                                                        }
                                                        tempAA.clear();
                                                        tempA.clear();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return max;

    }

    public void check(Student x) {
        ArrayList<ArrayList<ArrayList<Integer>>> temp = new ArrayList<>();
        for (int i = 0; i < x.courses.size(); i++)
            temp.add(x.courses.get(i).periods);
        x.able = take3(temp);
        x.dif = x.courses.size() - x.able;
        if (x.dif == 0)
            return;

        // ArrayList<ArrayList<Course>> myConflicts = new ArrayList<>();

        // looking for 2 at a time
        for (int a = 0; a < x.courses.size() - 1; a++)
            for (int b = a + 1; b < x.courses.size(); b++) {
                ArrayList<ArrayList<ArrayList<Integer>>> temp1 = new ArrayList<>();
                temp1.add(x.courses.get(a).periods);
                temp1.add(x.courses.get(b).periods);
                if (take3(temp1) < 2) {
                    ArrayList<Course> conflictedCourses = new ArrayList<>();
                    conflictedCourses.add(x.courses.get(a));
                    conflictedCourses.add(x.courses.get(b));
                    x.stuConflicts.add(conflictedCourses);

                }
            }

        // looking for 3 at a time
        boolean skip = false;
        for (int a = 0; a < x.courses.size() - 2; a++)
            for (int b = a + 1; b < x.courses.size() - 1; b++)
                for (int c = b + 1; c < x.courses.size(); c++) {
                    // checking to see if 2 of a,b,c are already conflicted
                    skip = false;
                    int counter = 0; // number of a,b,c that are in a particular conflict
                    for (int i = 0; i < x.stuConflicts.size(); i++) {
                        counter = 0;
                        if (x.stuConflicts.get(i).contains(x.courses.get(a)))
                            counter++;
                        if (x.stuConflicts.get(i).contains(x.courses.get(b)))
                            counter++;
                        if (x.stuConflicts.get(i).contains(x.courses.get(c)))
                            counter++;
                        if (counter == x.stuConflicts.get(i).size())
                            skip = true;
                    }
                    if (!skip) {

                        ArrayList<ArrayList<ArrayList<Integer>>> temp1 = new ArrayList<>();
                        temp1.add(x.courses.get(a).periods);
                        temp1.add(x.courses.get(b).periods);
                        temp1.add(x.courses.get(c).periods);
                        if (take3(temp1) < 3) {
                            ArrayList<Course> conflictedCourses = new ArrayList<>();
                            conflictedCourses.add(x.courses.get(a));
                            conflictedCourses.add(x.courses.get(b));
                            conflictedCourses.add(x.courses.get(c));
                            x.stuConflicts.add(conflictedCourses);

                        }
                    }
                }

        // looking for 4 at a time

        for (int a = 0; a < x.courses.size() - 3; a++)
            for (int b = a + 1; b < x.courses.size() - 2; b++)
                for (int c = b + 1; c < x.courses.size() - 1; c++)
                    for (int d = c + 1; d < x.courses.size(); d++) {
                        skip = false;
                        // System.out.print(a+" "+b+" "+c+" "+d);
                        // System.out.println(" "+x.courses.get(a)+" "+x.courses.get(b)+"
                        // "+x.courses.get(c)+" "+x.courses.get(d));
                        // checking to see if a,b,c,d are already conflicted
                        int counter = 0; // number of a,b,c,d that are in a particular conflict
                        for (int i = 0; i < x.stuConflicts.size(); i++) {
                            counter = 0;
                            if (x.stuConflicts.get(i).contains(x.courses.get(a)))
                                counter++;
                            if (x.stuConflicts.get(i).contains(x.courses.get(b)))
                                counter++;
                            if (x.stuConflicts.get(i).contains(x.courses.get(c)))
                                counter++;
                            if (x.stuConflicts.get(i).contains(x.courses.get(d)))
                                counter++;
                            if (counter == x.stuConflicts.get(i).size()) {
                                // System.out.println("Skipped: "+a+" "+b+" "+c+" "+d);
                                skip = true;
                                // System.out.println(x.stuConflicts.get(i).contains(x.courses.get(a))+"
                                // "+x.stuConflicts.get(i).contains(x.courses.get(b))+"
                                // "+x.stuConflicts.get(i).contains(x.courses.get(c))+"
                                // "+x.stuConflicts.get(i).contains(x.courses.get(d)));
                                // System.out.println(counter+" "+x.stuConflicts.get(i).size()+"
                                // "+x.stuConflicts.get(i)+" "+x.courses.get(a)+" "+x.courses.get(b)+"
                                // "+x.courses.get(c)+" "+x.courses.get(d));
                            }
                        }
                        if (!skip) {
                            // System.out.println("After skip"+a+" "+b+" "+c+" "+d);

                            // System.out.println(" "+x.courses.get(a)+" "+x.courses.get(b)+"
                            // "+x.courses.get(c)+" "+x.courses.get(d));

                            ArrayList<ArrayList<ArrayList<Integer>>> temp1 = new ArrayList<>();
                            temp1.add(x.courses.get(a).periods);
                            temp1.add(x.courses.get(b).periods);
                            temp1.add(x.courses.get(c).periods);
                            temp1.add(x.courses.get(d).periods);
                            if (take3(temp1) < 4) {

                                ArrayList<Course> conflictedCourses = new ArrayList<>();
                                conflictedCourses.add(x.courses.get(a));
                                conflictedCourses.add(x.courses.get(b));
                                conflictedCourses.add(x.courses.get(c));
                                conflictedCourses.add(x.courses.get(d));
                                x.stuConflicts.add(conflictedCourses);

                            }
                        }
                    }

        // looking for 5 at a time

        for (int a = 0; a < x.courses.size() - 4; a++)
            for (int b = a + 1; b < x.courses.size() - 3; b++)
                for (int c = b + 1; c < x.courses.size() - 2; c++)
                    for (int d = c + 1; d < x.courses.size() - 1; d++)
                        for (int e = d + 1; e < x.courses.size(); e++) {
                            // checking to see if a,b,c,d,e are already conflicted
                            skip = false;
                            int counter = 0; // number of a,b,c,d that are in a particular conflict
                            for (int i = 0; i < x.stuConflicts.size(); i++) {
                                counter = 0;
                                if (x.stuConflicts.get(i).contains(x.courses.get(a)))
                                    counter++;
                                if (x.stuConflicts.get(i).contains(x.courses.get(b)))
                                    counter++;
                                if (x.stuConflicts.get(i).contains(x.courses.get(c)))
                                    counter++;
                                if (x.stuConflicts.get(i).contains(x.courses.get(d)))
                                    counter++;
                                if (x.stuConflicts.get(i).contains(x.courses.get(e)))
                                    counter++;
                                if (counter == x.stuConflicts.get(i).size())
                                    skip = true;
                            }
                            if (!skip) {
                                ArrayList<ArrayList<ArrayList<Integer>>> temp1 = new ArrayList<>();
                                temp1.add(x.courses.get(a).periods);
                                temp1.add(x.courses.get(b).periods);
                                temp1.add(x.courses.get(c).periods);
                                temp1.add(x.courses.get(d).periods);
                                temp1.add(x.courses.get(e).periods);
                                if (take3(temp1) < 5) {
                                    ArrayList<Course> conflictedCourses = new ArrayList<>();
                                    conflictedCourses.add(x.courses.get(a));
                                    conflictedCourses.add(x.courses.get(b));
                                    conflictedCourses.add(x.courses.get(c));
                                    conflictedCourses.add(x.courses.get(d));
                                    conflictedCourses.add(x.courses.get(e));
                                    x.stuConflicts.add(conflictedCourses);

                                }
                            }
                        }

        // looking for 6 at a time

        for (int a = 0; a < x.courses.size() - 5; a++)
            for (int b = a + 1; b < x.courses.size() - 4; b++)
                for (int c = b + 1; c < x.courses.size() - 3; c++)
                    for (int d = c + 1; d < x.courses.size() - 2; d++)
                        for (int e = d + 1; e < x.courses.size() - 1; e++)
                            for (int f = e + 1; f < x.courses.size(); f++) {
                                // checking to see if a,b,c,d,e are already conflicted
                                skip = false;
                                int counter = 0; // number of a,b,c,d that are in a particular conflict
                                for (int i = 0; i < x.stuConflicts.size(); i++) {
                                    counter = 0;
                                    if (x.stuConflicts.get(i).contains(x.courses.get(a)))
                                        counter++;
                                    if (x.stuConflicts.get(i).contains(x.courses.get(b)))
                                        counter++;
                                    if (x.stuConflicts.get(i).contains(x.courses.get(c)))
                                        counter++;
                                    if (x.stuConflicts.get(i).contains(x.courses.get(d)))
                                        counter++;
                                    if (x.stuConflicts.get(i).contains(x.courses.get(e)))
                                        counter++;
                                    if (x.stuConflicts.get(i).contains(x.courses.get(f)))
                                        counter++;
                                    if (counter == x.stuConflicts.get(i).size())
                                        skip = true;
                                }
                                if (!skip) {
                                    ArrayList<ArrayList<ArrayList<Integer>>> temp1 = new ArrayList<>();
                                    temp1.add(x.courses.get(a).periods);
                                    temp1.add(x.courses.get(b).periods);
                                    temp1.add(x.courses.get(c).periods);
                                    temp1.add(x.courses.get(d).periods);
                                    temp1.add(x.courses.get(e).periods);
                                    temp1.add(x.courses.get(f).periods);
                                    if (take3(temp1) < 6) {
                                        ArrayList<Course> conflictedCourses = new ArrayList<>();
                                        conflictedCourses.add(x.courses.get(a));
                                        conflictedCourses.add(x.courses.get(b));
                                        conflictedCourses.add(x.courses.get(c));
                                        conflictedCourses.add(x.courses.get(d));
                                        conflictedCourses.add(x.courses.get(e));
                                        conflictedCourses.add(x.courses.get(f));
                                        x.stuConflicts.add(conflictedCourses);

                                    }
                                }
                            }

        // looking for 7 at a time

        for (int a = 0; a < x.courses.size() - 6; a++)
            for (int b = a + 1; b < x.courses.size() - 5; b++)
                for (int c = b + 1; c < x.courses.size() - 4; c++)
                    for (int d = c + 1; d < x.courses.size() - 3; d++)
                        for (int e = d + 1; e < x.courses.size() - 2; e++)
                            for (int f = e + 1; f < x.courses.size() - 1; f++)
                                for (int g = f + 1; g < x.courses.size(); g++) {
                                    // checking to see if a,b,c,d,e,f,g are already conflicted
                                    skip = false;
                                    int counter = 0; // number of a,b,c,d,e,f,g that are in a particular conflict
                                    for (int i = 0; i < x.stuConflicts.size(); i++) {
                                        counter = 0;
                                        if (x.stuConflicts.get(i).contains(x.courses.get(a)))
                                            counter++;
                                        if (x.stuConflicts.get(i).contains(x.courses.get(b)))
                                            counter++;
                                        if (x.stuConflicts.get(i).contains(x.courses.get(c)))
                                            counter++;
                                        if (x.stuConflicts.get(i).contains(x.courses.get(d)))
                                            counter++;
                                        if (x.stuConflicts.get(i).contains(x.courses.get(e)))
                                            counter++;
                                        if (x.stuConflicts.get(i).contains(x.courses.get(f)))
                                            counter++;
                                        if (x.stuConflicts.get(i).contains(x.courses.get(g)))
                                            counter++;
                                        if (counter == x.stuConflicts.get(i).size())
                                            skip = true;
                                    }
                                    if (!skip) {
                                        ArrayList<ArrayList<ArrayList<Integer>>> temp1 = new ArrayList<>();
                                        temp1.add(x.courses.get(a).periods);
                                        temp1.add(x.courses.get(b).periods);
                                        temp1.add(x.courses.get(c).periods);
                                        temp1.add(x.courses.get(d).periods);
                                        temp1.add(x.courses.get(e).periods);
                                        temp1.add(x.courses.get(f).periods);
                                        temp1.add(x.courses.get(g).periods);
                                        if (take3(temp1) < 7) {
                                            ArrayList<Course> conflictedCourses = new ArrayList<>();
                                            conflictedCourses.add(x.courses.get(a));
                                            conflictedCourses.add(x.courses.get(b));
                                            conflictedCourses.add(x.courses.get(c));
                                            conflictedCourses.add(x.courses.get(d));
                                            conflictedCourses.add(x.courses.get(e));
                                            conflictedCourses.add(x.courses.get(f));
                                            conflictedCourses.add(x.courses.get(g));
                                            x.stuConflicts.add(conflictedCourses);

                                        }
                                    }

                                }

        // looking for 8 at a time

        for (int a = 0; a < x.courses.size() - 7; a++)
            for (int b = a + 1; b < x.courses.size() - 6; b++)
                for (int c = b + 1; c < x.courses.size() - 5; c++)
                    for (int d = c + 1; d < x.courses.size() - 4; d++)
                        for (int e = d + 1; e < x.courses.size() - 3; e++)
                            for (int f = e + 1; f < x.courses.size() - 2; f++)
                                for (int g = f + 1; g < x.courses.size() - 1; g++)
                                    for (int h = g + 1; h < x.courses.size(); h++) {
                                        // checking to see if a,b,c,d,e,f,g are already conflicted
                                        skip = false;
                                        int counter = 0; // number of a,b,c,d,e,f,g that are in a particular conflict
                                        for (int i = 0; i < x.stuConflicts.size(); i++) {
                                            counter = 0;
                                            if (x.stuConflicts.get(i).contains(x.courses.get(a)))
                                                counter++;
                                            if (x.stuConflicts.get(i).contains(x.courses.get(b)))
                                                counter++;
                                            if (x.stuConflicts.get(i).contains(x.courses.get(c)))
                                                counter++;
                                            if (x.stuConflicts.get(i).contains(x.courses.get(d)))
                                                counter++;
                                            if (x.stuConflicts.get(i).contains(x.courses.get(e)))
                                                counter++;
                                            if (x.stuConflicts.get(i).contains(x.courses.get(f)))
                                                counter++;
                                            if (x.stuConflicts.get(i).contains(x.courses.get(g)))
                                                counter++;
                                            if (x.stuConflicts.get(i).contains(x.courses.get(h)))
                                                counter++;
                                            if (counter == x.stuConflicts.get(i).size())
                                                skip = true;
                                        }
                                        if (!skip) {
                                            ArrayList<ArrayList<ArrayList<Integer>>> temp1 = new ArrayList<>();
                                            temp1.add(x.courses.get(a).periods);
                                            temp1.add(x.courses.get(b).periods);
                                            temp1.add(x.courses.get(c).periods);
                                            temp1.add(x.courses.get(d).periods);
                                            temp1.add(x.courses.get(e).periods);
                                            temp1.add(x.courses.get(f).periods);
                                            temp1.add(x.courses.get(g).periods);
                                            temp1.add(x.courses.get(h).periods);
                                            if (take3(temp1) < 8) {
                                                ArrayList<Course> conflictedCourses = new ArrayList<>();
                                                conflictedCourses.add(x.courses.get(a));
                                                conflictedCourses.add(x.courses.get(b));
                                                conflictedCourses.add(x.courses.get(c));
                                                conflictedCourses.add(x.courses.get(d));
                                                conflictedCourses.add(x.courses.get(e));
                                                conflictedCourses.add(x.courses.get(f));
                                                conflictedCourses.add(x.courses.get(g));
                                                conflictedCourses.add(x.courses.get(h));
                                                x.stuConflicts.add(conflictedCourses);

                                            }
                                        }
                                    }

        // looking for 9 at a time

        for (int a = 0; a < x.courses.size() - 8; a++)
            for (int b = a + 1; b < x.courses.size() - 7; b++)
                for (int c = b + 1; c < x.courses.size() - 6; c++)
                    for (int d = c + 1; d < x.courses.size() - 5; d++)
                        for (int e = d + 1; e < x.courses.size() - 4; e++)
                            for (int f = e + 1; f < x.courses.size() - 3; f++)
                                for (int g = f + 1; g < x.courses.size() - 2; g++)
                                    for (int h = g + 1; h < x.courses.size() - 1; h++)
                                        for (int j = h + 1; j < x.courses.size(); j++) {
                                            // checking to see if a,b,c,d,e,f,g are already conflicted
                                            skip = false;
                                            int counter = 0; // number of a,b,c,d,e,f,g that are in a particular
                                            // conflict
                                            for (int i = 0; i < x.stuConflicts.size(); i++) {
                                                counter = 0;
                                                if (x.stuConflicts.get(i).contains(x.courses.get(a)))
                                                    counter++;
                                                if (x.stuConflicts.get(i).contains(x.courses.get(b)))
                                                    counter++;
                                                if (x.stuConflicts.get(i).contains(x.courses.get(c)))
                                                    counter++;
                                                if (x.stuConflicts.get(i).contains(x.courses.get(d)))
                                                    counter++;
                                                if (x.stuConflicts.get(i).contains(x.courses.get(e)))
                                                    counter++;
                                                if (x.stuConflicts.get(i).contains(x.courses.get(f)))
                                                    counter++;
                                                if (x.stuConflicts.get(i).contains(x.courses.get(g)))
                                                    counter++;
                                                if (x.stuConflicts.get(i).contains(x.courses.get(h)))
                                                    counter++;
                                                if (x.stuConflicts.get(i).contains(x.courses.get(j)))
                                                    counter++;
                                                if (counter == x.stuConflicts.get(i).size())
                                                    skip = true;
                                            }
                                            if (!skip) {
                                                ArrayList<ArrayList<ArrayList<Integer>>> temp1 = new ArrayList<>();
                                                temp1.add(x.courses.get(a).periods);
                                                temp1.add(x.courses.get(b).periods);
                                                temp1.add(x.courses.get(c).periods);
                                                temp1.add(x.courses.get(d).periods);
                                                temp1.add(x.courses.get(e).periods);
                                                temp1.add(x.courses.get(f).periods);
                                                temp1.add(x.courses.get(g).periods);
                                                temp1.add(x.courses.get(h).periods);
                                                temp1.add(x.courses.get(j).periods);
                                                if (take3(temp1) < 9) {
                                                    ArrayList<Course> conflictedCourses = new ArrayList<>();
                                                    conflictedCourses.add(x.courses.get(a));
                                                    conflictedCourses.add(x.courses.get(b));
                                                    conflictedCourses.add(x.courses.get(c));
                                                    conflictedCourses.add(x.courses.get(d));
                                                    conflictedCourses.add(x.courses.get(e));
                                                    conflictedCourses.add(x.courses.get(f));
                                                    conflictedCourses.add(x.courses.get(g));
                                                    conflictedCourses.add(x.courses.get(h));
                                                    conflictedCourses.add(x.courses.get(j));
                                                    x.stuConflicts.add(conflictedCourses);

                                                }
                                            }
                                        }

        // looking for 10 at a time

        for (int a = 0; a < x.courses.size() - 9; a++)
            for (int b = a + 1; b < x.courses.size() - 8; b++)
                for (int c = b + 1; c < x.courses.size() - 7; c++)
                    for (int d = c + 1; d < x.courses.size() - 6; d++)
                        for (int e = d + 1; e < x.courses.size() - 5; e++)
                            for (int f = e + 1; f < x.courses.size() - 4; f++)
                                for (int g = f + 1; g < x.courses.size() - 3; g++)
                                    for (int h = g + 1; h < x.courses.size() - 2; h++)
                                        for (int j = h + 1; j < x.courses.size() - 1; j++)
                                            for (int k = j + 1; k < x.courses.size(); k++) {
                                                // checking to see if a,b,c,d,e,f,g are already conflicted
                                                skip = false;
                                                int counter = 0; // number of a,b,c,d,e,f,g that are in a particular
                                                // conflict
                                                for (int i = 0; i < x.stuConflicts.size(); i++) {
                                                    counter = 0;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(a)))
                                                        counter++;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(b)))
                                                        counter++;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(c)))
                                                        counter++;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(d)))
                                                        counter++;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(e)))
                                                        counter++;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(f)))
                                                        counter++;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(g)))
                                                        counter++;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(h)))
                                                        counter++;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(j)))
                                                        counter++;
                                                    if (x.stuConflicts.get(i).contains(x.courses.get(k)))
                                                        counter++;

                                                    if (counter == x.stuConflicts.get(i).size())
                                                        skip = true;
                                                }
                                                if (!skip) {
                                                    ArrayList<ArrayList<ArrayList<Integer>>> temp1 = new ArrayList<>();
                                                    temp1.add(x.courses.get(a).periods);
                                                    temp1.add(x.courses.get(b).periods);
                                                    temp1.add(x.courses.get(c).periods);
                                                    temp1.add(x.courses.get(d).periods);
                                                    temp1.add(x.courses.get(e).periods);
                                                    temp1.add(x.courses.get(f).periods);
                                                    temp1.add(x.courses.get(g).periods);
                                                    temp1.add(x.courses.get(h).periods);
                                                    temp1.add(x.courses.get(j).periods);
                                                    temp1.add(x.courses.get(k).periods);
                                                    if (take3(temp1) < 10) {
                                                        ArrayList<Course> conflictedCourses = new ArrayList<>();
                                                        conflictedCourses.add(x.courses.get(a));
                                                        conflictedCourses.add(x.courses.get(b));
                                                        conflictedCourses.add(x.courses.get(c));
                                                        conflictedCourses.add(x.courses.get(d));
                                                        conflictedCourses.add(x.courses.get(e));
                                                        conflictedCourses.add(x.courses.get(f));
                                                        conflictedCourses.add(x.courses.get(g));
                                                        conflictedCourses.add(x.courses.get(h));
                                                        conflictedCourses.add(x.courses.get(j));
                                                        conflictedCourses.add(x.courses.get(k));
                                                        x.stuConflicts.add(conflictedCourses);

                                                    }
                                                }
                                            }

    }

    public void check(Course x) {
        int sect = x.numSecs;
        check(x, sect);
    }

    public void check(Course x, boolean con) {
        x.consec = con;
        check(x);
    }

    public void check(Course x, int sect) {
        ArrayList<ArrayList<Integer>> numPermsArray = new ArrayList<>();
        // for (int i = 0; i < x.roster.size(); i++)// for each student on the roster
        // {
        // 	x.roster.get(i).temp1.clear();
        // }

        for (int i = 0; i < x.roster.size(); i++)// for each student on the roster
        {
            Student.maxP = 0;
            for (int j = 0; j < x.roster.get(i).courses.size(); j++)// for each course of those students
            {
                // populates temp1 w periods, for each kid, doesn't populate with course we are checking (thats the part after &&) this is where we have to make the adjustment for double scheduled sections such as (3,4) and (3,4) so that it doesn't add twice
                if (x.roster.get(i).courses.get(j).posted && !x.equals(x.roster.get(i).courses.get(j))) {
                    x.roster.get(i).temp1.add(x.roster.get(i).courses.get(j).periods);
                    /* above adds all courses to temp1, below removes the duplicates, hopefully */
                    for (int q = x.roster.get(i).temp1.get(x.roster.get(i).temp1.size() - 1).size() - 1; q > 0; q--)// working on this, very unsure
                    {
                        for (int r = q - 1; r >= 0; r--) {
                            if (x.roster.get(i).temp1.get(x.roster.get(i).temp1.size() - 1).get(q).equals(x.roster.get(i).temp1.get(x.roster.get(i).temp1.size() - 1).get(r))) {
                                x.roster.get(i).temp1.get(x.roster.get(i).temp1.size() - 1).remove(q);
                                break;
                            }
                        }
                    }
                }
            }
            x.roster.get(i).before = take3(x.roster.get(i).temp1); // before should have # classes schedulable before trying this one.
//            System.out.println(x.roster.get(i).name + " " + x.roster.get(i).temp1);
            int count = 0;// for maxPermsArray
            if (sect == 1 && x.lab == 1) // 1 section, no lab - just regular class
            {
                for (int f = 1; f <= 15; f = f + 2) {

                    ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                    ArrayList<Integer> trying1 = new ArrayList<>();
                    trying1.add(f);
                    trying1.add(f + 1);
                    trying.add(trying1);
                    if (!Master.containsAll(x.periods, trying)) { // Has to do with dealing with checking a course that
                        // already has sections scheduled
                        continue;// if trying doesn't contain all of the periods already scheduled, skip to next
                        // period.
                    }
                    x.roster.get(i).temp1.add(trying);
                    if (i == 0) // just for first kid in roster
                    {
                        x.periodsTrying.add("" + (f + 1) / 2);
                        numPermsArray.add(new ArrayList<>());
                    }

                    x.roster.get(i).conflict.add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                    numPermsArray.get(count).add(Student.maxP);//
                    count++;
                    Student.maxP = 0;
                    x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for next round, not
                    // sure this is necessary
                }

            }

            if (sect == 2 && x.lab == 1) // 2 sections, no lab - just regular class
            {
                for (int e = 1; e <= 13; e += 2) {
                    for (int f = e + 2; f <= 15; f = f + 2) {
                        // System.out.println(e+" "+(e+1)+" "+f+" "+(f+1));
                        ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                        ArrayList<Integer> trying1 = new ArrayList<>();
                        trying1.add(e);
                        trying1.add(e + 1);
                        ArrayList<Integer> trying2 = new ArrayList<>();
                        trying2.add(f);
                        trying2.add(f + 1);
                        trying.add(trying1);
                        trying.add(trying2);
                        if (!Master.containsAll(x.periods, trying)) {
                            continue;
                        }

                        x.roster.get(i).temp1.add(trying);

                        if (i == 0) {
                            x.periodsTrying.add("" + (e + 1) / 2 + " " + (f + 1) / 2);
                            numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                        }

                        x.roster.get(i).conflict.add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                        numPermsArray.get(count).add(Student.maxP);//
                        count++;
                        Student.maxP = 0;
                        x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for next round,
                        // not sure this is necessary
                    }
                }

            }

            if (sect == 3 && x.lab == 1) {
                for (int d = 1; d <= 11; d += 2) {
                    for (int e = d + 2; e <= 13; e += 2) {
                        for (int f = e + 2; f <= 15; f = f + 2) {
                            // System.out.println(d+" "+(d+1)+" "+e+" "+(e+1)+" "+f+" "+(f+1));
                            ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                            ArrayList<Integer> trying1 = new ArrayList<>();
                            trying1.add(d);
                            trying1.add(d + 1);
                            ArrayList<Integer> trying2 = new ArrayList<>();
                            trying2.add(e);
                            trying2.add(e + 1);
                            ArrayList<Integer> trying3 = new ArrayList<>();
                            trying3.add(f);
                            trying3.add(f + 1);
                            trying.add(trying1);
                            trying.add(trying2);
                            trying.add(trying3);
                            if (!Master.containsAll(x.periods, trying)) {
                                continue;
                            }
                            x.roster.get(i).temp1.add(trying);
                            if (i == 0) {
                                x.periodsTrying.add("" + (d + 1) / 2 + " " + (e + 1) / 2 + " " + (f + 1) / 2);
                                numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                            }

                            x.roster.get(i).conflict.add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                            numPermsArray.get(count).add(Student.maxP);//
                            count++;
                            Student.maxP = 0;
                            x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for next
                            // round, not sure this is
                            // necessary
                        }

                    }
                }
            }

            if (sect == 4 && x.lab == 1) {
                for (int c = 1; c <= 9; c += 2)

                    for (int d = c + 2; d <= 11; d += 2)

                        for (int e = d + 2; e <= 13; e += 2) {
                            for (int f = e + 2; f <= 15; f = f + 2) {
                                // System.out.println(c+" "+(c+1)+" "+d+" "+(d+1)+" "+e+" "+(e+1)+" "+f+"
                                // "+(f+1));
                                ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                ArrayList<Integer> trying1 = new ArrayList<>();
                                trying1.add(c);
                                trying1.add(c + 1);
                                ArrayList<Integer> trying2 = new ArrayList<>();
                                trying2.add(d);
                                trying2.add(d + 1);
                                ArrayList<Integer> trying3 = new ArrayList<>();
                                trying3.add(e);
                                trying3.add(e + 1);
                                ArrayList<Integer> trying4 = new ArrayList<>();
                                trying4.add(f);
                                trying4.add(f + 1);
                                trying.add(trying1);
                                trying.add(trying2);
                                trying.add(trying3);
                                trying.add(trying4);
                                if (!Master.containsAll(x.periods, trying)) {
                                    continue;
                                }
                                x.roster.get(i).temp1.add(trying);
                                if (i == 0) {
                                    x.periodsTrying.add("" + (c + 1) / 2 + " " + (d + 1) / 2 + " " + (e + 1) / 2 + " "
                                            + (f + 1) / 2);
                                    numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                                }

                                x.roster.get(i).conflict
                                        .add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                numPermsArray.get(count).add(Student.maxP);//
                                count++;
                                Student.maxP = 0;
                                x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for
                                // next round, not sure
                                // this is necessary
                            }
                        }

            }

            if (sect == 5 && x.lab == 1) {
                for (int b = 1; b <= 7; b += 2)

                    for (int c = b + 2; c <= 9; c += 2)

                        for (int d = c + 2; d <= 11; d += 2)

                            for (int e = d + 2; e <= 13; e += 2) {
                                for (int f = e + 2; f <= 15; f = f + 2) {
                                    // System.out.println(b+" "+(b+1)+" "+c+" "+(c+1)+" "+d+" "+(d+1)+" "+e+"
                                    // "+(e+1)+" "+f+" "+(f+1));
                                    ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                    ArrayList<Integer> trying1 = new ArrayList<>();
                                    trying1.add(b);
                                    trying1.add(b + 1);
                                    ArrayList<Integer> trying2 = new ArrayList<>();
                                    trying2.add(c);
                                    trying2.add(c + 1);
                                    ArrayList<Integer> trying3 = new ArrayList<>();
                                    trying3.add(d);
                                    trying3.add(d + 1);
                                    ArrayList<Integer> trying4 = new ArrayList<>();
                                    trying4.add(e);
                                    trying4.add(e + 1);
                                    ArrayList<Integer> trying5 = new ArrayList<>();
                                    trying5.add(f);
                                    trying5.add(f + 1);
                                    trying.add(trying1);
                                    trying.add(trying2);
                                    trying.add(trying3);
                                    trying.add(trying4);
                                    trying.add(trying5);
                                    if (!Master.containsAll(x.periods, trying)) {
                                        continue;
                                    }
                                    x.roster.get(i).temp1.add(trying);
                                    if (i == 0) {
                                        x.periodsTrying.add("" + (b + 1) / 2 + " " + (c + 1) / 2 + " " + (d + 1) / 2
                                                + " " + (e + 1) / 2 + " " + (f + 1) / 2);
                                        numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                                    }

                                    x.roster.get(i).conflict
                                            .add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                    numPermsArray.get(count).add(Student.maxP);//
                                    count++;
                                    Student.maxP = 0;
                                    x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for
                                    // next round

                                }
                            }

            }

            if (sect == 6 && x.lab == 1) {
                for (int a = 1; a <= 5; a += 2)

                    for (int b = a + 2; b <= 7; b += 2)

                        for (int c = b + 2; c <= 9; c += 2)

                            for (int d = c + 2; d <= 11; d += 2)

                                for (int e = d + 2; e <= 13; e += 2) {
                                    for (int f = e + 2; f <= 15; f = f + 2) {
                                        // System.out.println(a+" " + (a+1)+" "+b+" "+(b+1)+" "+c+" "+(c+1)+" "+d+"
                                        // "+(d+1)+" "+e+" "+(e+1)+" "+f+" "+(f+1));
                                        ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                        ArrayList<Integer> trying1 = new ArrayList<>();
                                        trying1.add(a);
                                        trying1.add(a + 1);
                                        ArrayList<Integer> trying2 = new ArrayList<>();
                                        trying2.add(b);
                                        trying2.add(b + 1);
                                        ArrayList<Integer> trying3 = new ArrayList<>();
                                        trying3.add(c);
                                        trying3.add(c + 1);
                                        ArrayList<Integer> trying4 = new ArrayList<>();
                                        trying4.add(d);
                                        trying4.add(d + 1);
                                        ArrayList<Integer> trying5 = new ArrayList<>();
                                        trying5.add(e);
                                        trying5.add(e + 1);
                                        ArrayList<Integer> trying6 = new ArrayList<>();
                                        trying6.add(f);
                                        trying6.add(f + 1);
                                        trying.add(trying1);
                                        trying.add(trying2);
                                        trying.add(trying3);
                                        trying.add(trying4);
                                        trying.add(trying5);
                                        trying.add(trying6);
                                        if (!Master.containsAll(x.periods, trying)) {
                                            continue;
                                        }
                                        x.roster.get(i).temp1.add(trying);
                                        if (i == 0) {
                                            x.periodsTrying.add("" + (a + 1) / 2 + " " + (b + 1) / 2 + " " + (c + 1) / 2
                                                    + " " + (d + 1) / 2 + " " + (e + 1) / 2 + " " + (f + 1) / 2);
                                            numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                                        }

                                        x.roster.get(i).conflict
                                                .add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                        numPermsArray.get(count).add(Student.maxP);//
                                        count++;
                                        Student.maxP = 0;
                                        x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying
                                        // for next
                                        // round, not
                                        // sure this is
                                        // necessary
                                    }
                                }

            }

            if (sect == 1 && x.lab == 0.5) {
                for (int a = 1; a <= 16; a++) {
                    // System.out.println(a);
                    ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                    ArrayList<Integer> trying1 = new ArrayList<>();
                    trying1.add(a);
                    trying.add(trying1);
                    if (!Master.containsAll(x.periods, trying)) {
                        continue;
                    }
                    x.roster.get(i).temp1.add(trying);
                    String letA;
                    if (i == 0) {
                        if (a % 2 == 1)
                            letA = "A";
                        else
                            letA = "B";

                        x.periodsTrying.add("" + (a + 1) / 2 + letA);
                        numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                    }

                    x.roster.get(i).conflict.add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                    numPermsArray.get(count).add(Student.maxP);//
                    count++;
                    Student.maxP = 0;
                    x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for next round, not
                    // sure this is necessary
                }
            }

            if (sect == 2 && x.lab == 0.5) {
                for (int a = 1; a <= 15; a++)
                    for (int b = a + 1; b <= 16; b++) {
                        // System.out.println(a+" "+b);
                        ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                        ArrayList<Integer> trying1 = new ArrayList<>();
                        trying1.add(a);
                        ArrayList<Integer> trying2 = new ArrayList<>();
                        trying2.add(b);
                        trying.add(trying1);
                        trying.add(trying2);
                        if (!Master.containsAll(x.periods, trying)) {
                            continue;
                        }
                        x.roster.get(i).temp1.add(trying);
                        String letA;
                        String letB;
                        if (i == 0) {
                            if (a % 2 == 1)
                                letA = "A";
                            else
                                letA = "B";
                            if (b % 2 == 1)
                                letB = "A";
                            else
                                letB = "B";

                            x.periodsTrying.add("" + (a + 1) / 2 + letA + " " + (b + 1) / 2 + letB);
                            numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                        }

                        x.roster.get(i).conflict.add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                        numPermsArray.get(count).add(Student.maxP);//
                        count++;
                        Student.maxP = 0;
                        x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for next round,
                        // not sure this is necessary
                    }
            }

            if (sect == 3 && x.lab == 0.5) {
                for (int a = 1; a <= 14; a++)
                    for (int b = a + 1; b <= 15; b++)
                        for (int c = b + 1; c <= 16; c++) {
                            // System.out.println(a+" "+b+" "+c);
                            ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                            ArrayList<Integer> trying1 = new ArrayList<>();
                            trying1.add(a);
                            ArrayList<Integer> trying2 = new ArrayList<>();
                            trying2.add(b);
                            ArrayList<Integer> trying3 = new ArrayList<>();
                            trying3.add(c);
                            trying.add(trying1);
                            trying.add(trying2);
                            trying.add(trying3);
                            if (!Master.containsAll(x.periods, trying)) {
                                continue;
                            }
                            x.roster.get(i).temp1.add(trying);
                            String letA;
                            String letB;
                            String letC;
                            if (i == 0) {
                                if (a % 2 == 1)
                                    letA = "A";
                                else
                                    letA = "B";
                                if (b % 2 == 1)
                                    letB = "A";
                                else
                                    letB = "B";
                                if (c % 2 == 1)
                                    letC = "A";
                                else
                                    letC = "B";

                                x.periodsTrying.add(
                                        "" + (a + 1) / 2 + letA + " " + (b + 1) / 2 + letB + " " + (c + 1) / 2 + letC);
                                numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                            }

                            x.roster.get(i).conflict.add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                            numPermsArray.get(count).add(Student.maxP);//
                            count++;
                            Student.maxP = 0;
                            x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for next
                            // round, not sure this is
                            // necessary
                        }
            }

            if (sect == 4 && x.lab == 0.5) {
                for (int a = 1; a <= 13; a++)
                    for (int b = a + 1; b <= 14; b++)
                        for (int c = b + 1; c <= 15; c++)
                            for (int d = c + 1; d <= 16; d++) {
                                // System.out.println(a+" "+b+" "+c+" "+d);
                                ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                ArrayList<Integer> trying1 = new ArrayList<>();
                                trying1.add(a);
                                ArrayList<Integer> trying2 = new ArrayList<>();
                                trying2.add(b);
                                ArrayList<Integer> trying3 = new ArrayList<>();
                                trying3.add(c);
                                ArrayList<Integer> trying4 = new ArrayList<>();
                                trying4.add(d);
                                trying.add(trying1);
                                trying.add(trying2);
                                trying.add(trying3);
                                trying.add(trying4);
                                if (!Master.containsAll(x.periods, trying)) {
                                    continue;
                                }
                                x.roster.get(i).temp1.add(trying);
                                String letA;
                                String letB;
                                String letC;
                                String letD;
                                if (i == 0) {
                                    if (a % 2 == 1)
                                        letA = "A";
                                    else
                                        letA = "B";
                                    if (b % 2 == 1)
                                        letB = "A";
                                    else
                                        letB = "B";
                                    if (c % 2 == 1)
                                        letC = "A";
                                    else
                                        letC = "B";
                                    if (d % 2 == 1)
                                        letD = "A";
                                    else
                                        letD = "B";
                                    x.periodsTrying.add("" + (a + 1) / 2 + letA + " " + (b + 1) / 2 + letB + " "
                                            + (c + 1) / 2 + letC + " " + (d + 1) / 2 + letD);
                                    numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                                }

                                x.roster.get(i).conflict
                                        .add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                numPermsArray.get(count).add(Student.maxP);//
                                count++;
                                Student.maxP = 0;
                                x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for
                                // next round, not sure
                                // this is necessary
                            }
            }

            if (sect == 5 && x.lab == 0.5) {
                for (int a = 1; a <= 12; a++)
                    for (int b = a + 1; b <= 13; b++)
                        for (int c = b + 1; c <= 14; c++)
                            for (int d = c + 1; d <= 15; d++)
                                for (int e = d + 1; e <= 16; e++) {
                                    // System.out.println(a+" "+b+" "+c+" "+d+" "+e);
                                    ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                    ArrayList<Integer> trying1 = new ArrayList<>();
                                    trying1.add(a);
                                    ArrayList<Integer> trying2 = new ArrayList<>();
                                    trying2.add(b);
                                    ArrayList<Integer> trying3 = new ArrayList<>();
                                    trying3.add(c);
                                    ArrayList<Integer> trying4 = new ArrayList<>();
                                    trying4.add(d);
                                    ArrayList<Integer> trying5 = new ArrayList<>();
                                    trying5.add(e);
                                    trying.add(trying1);
                                    trying.add(trying2);
                                    trying.add(trying3);
                                    trying.add(trying4);
                                    trying.add(trying5);
                                    if (!Master.containsAll(x.periods, trying)) {
                                        continue;
                                    }
                                    x.roster.get(i).temp1.add(trying);
                                    String letA;
                                    String letB;
                                    String letC;
                                    String letD;
                                    String letE;
                                    if (i == 0) {
                                        if (a % 2 == 1)
                                            letA = "A";
                                        else
                                            letA = "B";
                                        if (b % 2 == 1)
                                            letB = "A";
                                        else
                                            letB = "B";
                                        if (c % 2 == 1)
                                            letC = "A";
                                        else
                                            letC = "B";
                                        if (d % 2 == 1)
                                            letD = "A";
                                        else
                                            letD = "B";
                                        if (e % 2 == 1)
                                            letE = "A";
                                        else
                                            letE = "B";
                                        x.periodsTrying.add(
                                                "" + (a + 1) / 2 + letA + " " + (b + 1) / 2 + letB + " " + (c + 1) / 2
                                                        + letC + " " + (d + 1) / 2 + letD + " " + (e + 1) / 2 + letE);
                                        numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                                    }

                                    x.roster.get(i).conflict
                                            .add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                    numPermsArray.get(count).add(Student.maxP);//
                                    count++;
                                    Student.maxP = 0;
                                    x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for
                                    // next round, not
                                    // sure this is
                                    // necessary
                                }
            }

            if (sect == 6 && x.lab == 0.5) {
                for (int a = 1; a <= 11; a++)
                    for (int b = a + 1; b <= 12; b++)
                        for (int c = b + 1; c <= 13; c++)
                            for (int d = c + 1; d <= 14; d++)
                                for (int e = d + 1; e <= 15; e++)
                                    for (int f = e + 1; f <= 16; f++) {
                                        // System.out.println(a+" "+b+" "+c+" "+d+" "+e+" "+f);
                                        ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                        ArrayList<Integer> trying1 = new ArrayList<>();
                                        trying1.add(a);
                                        ArrayList<Integer> trying2 = new ArrayList<>();
                                        trying2.add(b);
                                        ArrayList<Integer> trying3 = new ArrayList<>();
                                        trying3.add(c);
                                        ArrayList<Integer> trying4 = new ArrayList<>();
                                        trying4.add(d);
                                        ArrayList<Integer> trying5 = new ArrayList<>();
                                        trying5.add(e);
                                        ArrayList<Integer> trying6 = new ArrayList<>();
                                        trying6.add(f);
                                        trying.add(trying1);
                                        trying.add(trying2);
                                        trying.add(trying3);
                                        trying.add(trying4);
                                        trying.add(trying5);
                                        trying.add(trying6);
                                        if (!Master.containsAll(x.periods, trying)) {
                                            continue;
                                        }
                                        x.roster.get(i).temp1.add(trying);
                                        String letA;
                                        String letB;
                                        String letC;
                                        String letD;
                                        String letE;
                                        String letF;
                                        if (i == 0) {
                                            if (a % 2 == 1)
                                                letA = "A";
                                            else
                                                letA = "B";
                                            if (b % 2 == 1)
                                                letB = "A";
                                            else
                                                letB = "B";
                                            if (c % 2 == 1)
                                                letC = "A";
                                            else
                                                letC = "B";
                                            if (d % 2 == 1)
                                                letD = "A";
                                            else
                                                letD = "B";
                                            if (e % 2 == 1)
                                                letE = "A";
                                            else
                                                letE = "B";
                                            if (f % 2 == 1)
                                                letF = "A";
                                            else
                                                letF = "B";
                                            x.periodsTrying.add("" + (a + 1) / 2 + letA + " " + (b + 1) / 2 + letB + " "
                                                    + (c + 1) / 2 + letC + " " + (d + 1) / 2 + letD + " " + (e + 1) / 2
                                                    + letE + " " + (f + 1) / 2 + letF);
                                            numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                                        }

                                        x.roster.get(i).conflict
                                                .add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                        numPermsArray.get(count).add(Student.maxP);//
                                        count++;
                                        Student.maxP = 0;
                                        x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying
                                        // for next
                                        // round, not
                                        // sure this is
                                        // necessary
                                    }
            }

            if (x.lab == 1.5) {
                int[][] triers;
                String[][] tryString;
                if (x.consec) {
                    triers = new int[32][3];
                    tryString = new String[32][3];
                    triers[0][0] = 1;
                    triers[0][1] = 2;
                    triers[0][2] = 3;

                    tryString[0][0] = "1A";
                    tryString[0][1] = "1B";
                    tryString[0][2] = "2A";

                    triers[1][0] = 1;
                    triers[1][1] = 2;
                    triers[1][2] = 4;

                    tryString[1][0] = "1A";
                    tryString[1][1] = "1B";
                    tryString[1][2] = "2B";

                    triers[2][0] = 1;
                    triers[2][1] = 3;
                    triers[2][2] = 4;

                    tryString[2][0] = "1A";
                    tryString[2][1] = "2A";
                    tryString[2][2] = "2B";

                    triers[3][0] = 2;
                    triers[3][1] = 3;
                    triers[3][2] = 4;

                    tryString[3][0] = "1B";
                    tryString[3][1] = "2A";
                    tryString[3][2] = "2B";

                    triers[4][0] = 3;
                    triers[4][1] = 4;
                    triers[4][2] = 5;

                    tryString[4][0] = "2A";
                    tryString[4][1] = "2B";
                    tryString[4][2] = "3A";

                    triers[5][0] = 3;
                    triers[5][1] = 4;
                    triers[5][2] = 6;

                    tryString[5][0] = "2A";
                    tryString[5][1] = "2B";
                    tryString[5][2] = "3B";

                    triers[6][0] = 3;
                    triers[6][1] = 5;
                    triers[6][2] = 6;

                    tryString[6][0] = "2A";
                    tryString[6][1] = "3A";
                    tryString[6][2] = "3B";

                    triers[7][0] = 4;
                    triers[7][1] = 5;
                    triers[7][2] = 6;

                    tryString[7][0] = "2B";
                    tryString[7][1] = "3A";
                    tryString[7][2] = "3B";

                    triers[8][0] = 5;
                    triers[8][1] = 6;
                    triers[8][2] = 7;

                    tryString[8][0] = "3A";
                    tryString[8][1] = "3B";
                    tryString[8][2] = "4A";

                    triers[9][0] = 5;
                    triers[9][1] = 6;
                    triers[9][2] = 8;

                    tryString[9][0] = "3A";
                    tryString[9][1] = "3B";
                    tryString[9][2] = "4B";

                    triers[10][0] = 5;
                    triers[10][1] = 7;
                    triers[10][2] = 8;

                    tryString[10][0] = "3A";
                    tryString[10][1] = "4A";
                    tryString[10][2] = "4B";

                    triers[11][0] = 6;
                    triers[11][1] = 7;
                    triers[11][2] = 8;

                    tryString[11][0] = "3B";
                    tryString[11][1] = "4A";
                    tryString[11][2] = "4B";

                    triers[12][0] = 7;
                    triers[12][1] = 8;
                    triers[12][2] = 9;

                    tryString[12][0] = "4A";
                    tryString[12][1] = "4B";
                    tryString[12][2] = "5A";

                    triers[13][0] = 7;
                    triers[13][1] = 8;
                    triers[13][2] = 10;

                    tryString[13][0] = "4A";
                    tryString[13][1] = "4B";
                    tryString[13][2] = "5B";

                    triers[14][0] = 7;
                    triers[14][1] = 9;
                    triers[14][2] = 10;

                    tryString[14][0] = "4A";
                    tryString[14][1] = "5A";
                    tryString[14][2] = "5B";

                    triers[15][0] = 8;
                    triers[15][1] = 9;
                    triers[15][2] = 10;

                    tryString[15][0] = "4B";
                    tryString[15][1] = "5A";
                    tryString[15][2] = "5B";

                    triers[16][0] = 9;
                    triers[16][1] = 10;
                    triers[16][2] = 11;

                    tryString[16][0] = "5A";
                    tryString[16][1] = "5B";
                    tryString[16][2] = "6A";

                    triers[17][0] = 9;
                    triers[17][1] = 10;
                    triers[17][2] = 12;

                    tryString[17][0] = "5A";
                    tryString[17][1] = "5B";
                    tryString[17][2] = "6B";

                    triers[18][0] = 9;
                    triers[18][1] = 11;
                    triers[18][2] = 12;

                    tryString[18][0] = "5A";
                    tryString[18][1] = "6A";
                    tryString[18][2] = "6B";

                    triers[19][0] = 10;
                    triers[19][1] = 11;
                    triers[19][2] = 12;

                    tryString[19][0] = "5B";
                    tryString[19][1] = "6A";
                    tryString[19][2] = "6B";

                    triers[20][0] = 11;
                    triers[20][1] = 12;
                    triers[20][2] = 13;

                    tryString[20][0] = "6A";
                    tryString[20][1] = "6B";
                    tryString[20][2] = "7A";

                    triers[21][0] = 11;
                    triers[21][1] = 12;
                    triers[21][2] = 14;

                    tryString[21][0] = "6A";
                    tryString[21][1] = "6B";
                    tryString[21][2] = "7B";

                    triers[22][0] = 11;
                    triers[22][1] = 13;
                    triers[22][2] = 14;

                    tryString[22][0] = "6A";
                    tryString[22][1] = "7A";
                    tryString[22][2] = "7B";

                    triers[23][0] = 12;
                    triers[23][1] = 13;
                    triers[23][2] = 14;

                    tryString[23][0] = "6B";
                    tryString[23][1] = "7A";
                    tryString[23][2] = "7B";

                    triers[24][0] = 13;
                    triers[24][1] = 14;
                    triers[24][2] = 15;

                    tryString[24][0] = "7A";
                    tryString[24][1] = "7B";
                    tryString[24][2] = "8A";

                    triers[25][0] = 13;
                    triers[25][1] = 14;
                    triers[25][2] = 16;

                    tryString[25][0] = "7A";
                    tryString[25][1] = "7B";
                    tryString[25][2] = "8B";

                    triers[26][0] = 13;
                    triers[26][1] = 15;
                    triers[26][2] = 16;

                    tryString[26][0] = "7A";
                    tryString[26][1] = "8A";
                    tryString[26][2] = "8B";

                    triers[27][0] = 14;
                    triers[27][1] = 15;
                    triers[27][2] = 16;

                    tryString[27][0] = "7B";
                    tryString[27][1] = "8A";
                    tryString[27][2] = "8B";

                    triers[28][0] = 15;
                    triers[28][1] = 16;
                    triers[28][2] = 1;

                    tryString[28][0] = "8A";
                    tryString[28][1] = "8B";
                    tryString[28][2] = "1A";

                    triers[29][0] = 15;
                    triers[29][1] = 16;
                    triers[29][2] = 2;

                    tryString[29][0] = "8A";
                    tryString[29][1] = "8B";
                    tryString[29][2] = "1B";

                    triers[30][0] = 15;
                    triers[30][1] = 1;
                    triers[30][2] = 2;

                    tryString[30][0] = "8A";
                    tryString[30][1] = "1A";
                    tryString[30][2] = "1B";

                    triers[31][0] = 16;
                    triers[31][1] = 1;
                    triers[31][2] = 2;

                    tryString[31][0] = "8B";
                    tryString[31][1] = "1A";
                    tryString[31][2] = "1B";

                } else {
                    String[] table = new String[17];
                    table[0] = "";
                    table[1] = "1A";
                    table[2] = "1B";
                    table[3] = "2A";
                    table[4] = "2B";
                    table[5] = "3A";
                    table[6] = "3B";
                    table[7] = "4A";
                    table[8] = "4B";
                    table[9] = "5A";
                    table[10] = "5B";
                    table[11] = "6A";
                    table[12] = "6B";
                    table[13] = "7A";
                    table[14] = "7B";
                    table[15] = "8A";
                    table[16] = "8B";
                    triers = new int[112][3];
                    tryString = new String[112][3];
                    int d = 0;
                    for (int a = 1; a < 16; a = a + 2) {
                        int b = a + 1;
                        for (int c = 1; c < 17; c++) {

                            if (c == a || c == b)
                                continue;
                            triers[d][0] = a;
                            triers[d][1] = b;
                            triers[d][2] = c;
                            tryString[d][0] = table[a];
                            tryString[d][1] = table[b];
                            tryString[d][2] = table[c];
                            d++;
                        }

                    }

                }

                if (sect == 1) {
                    for (int a = 0; a < triers.length; a++) {
                        ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                        ArrayList<Integer> trying1 = new ArrayList<>();
                        trying1.add(triers[a][0]);
                        trying1.add(triers[a][1]);
                        trying1.add(triers[a][2]);
                        trying.add(trying1);
                        if (!Master.containsAll(x.periods, trying)) {
                            continue;
                        }
                        x.roster.get(i).temp1.add(trying);
                        if (i == 0) {
                            x.periodsTrying.add("" + tryString[a][0] + tryString[a][1] + tryString[a][2]);
                            numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                        }

                        x.roster.get(i).conflict.add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                        numPermsArray.get(count).add(Student.maxP);//
                        count++;
                        Student.maxP = 0;
                        x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for next round,
                        // not sure this is necessary
                    }
                }
                if (sect == 2) {
                    for (int a = 0; a < triers.length - 1; a++)
                        for (int b = a + 1; b < triers.length; b++) {
                            ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                            ArrayList<Integer> trying1 = new ArrayList<>();
                            trying1.add(triers[a][0]);
                            trying1.add(triers[a][1]);
                            trying1.add(triers[a][2]);
                            trying.add(trying1);
                            ArrayList<Integer> trying2 = new ArrayList<>();
                            trying2.add(triers[b][0]);
                            trying2.add(triers[b][1]);
                            trying2.add(triers[b][2]);
                            trying.add(trying2);
                            if (!Master.containsAll(x.periods, trying)) {
                                continue;
                            }
                            x.roster.get(i).temp1.add(trying);
                            if (i == 0) {
                                x.periodsTrying.add("" + tryString[a][0] + "" + tryString[a][1] + "" + tryString[a][2]
                                        + " " + tryString[b][0] + "" + tryString[b][1] + "" + tryString[b][2]);
                                numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                            }

                            x.roster.get(i).conflict.add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                            numPermsArray.get(count).add(Student.maxP);//
                            count++;
                            Student.maxP = 0;
                            x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for next
                            // round, not sure this is
                            // necessary
                        }
                }
                if (sect == 3) {
                    for (int a = 0; a < triers.length - 2; a++)
                        for (int b = a + 1; b < triers.length - 1; b++)
                            for (int c = b + 1; c < triers.length; c++) {
                                ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                ArrayList<Integer> trying1 = new ArrayList<>();
                                trying1.add(triers[a][0]);
                                trying1.add(triers[a][1]);
                                trying1.add(triers[a][2]);
                                trying.add(trying1);
                                ArrayList<Integer> trying2 = new ArrayList<>();
                                trying2.add(triers[b][0]);
                                trying2.add(triers[b][1]);
                                trying2.add(triers[b][2]);
                                trying.add(trying2);
                                ArrayList<Integer> trying3 = new ArrayList<>();
                                trying3.add(triers[c][0]);
                                trying3.add(triers[c][1]);
                                trying3.add(triers[c][2]);
                                trying.add(trying3);
                                if (!Master.containsAll(x.periods, trying)) {
                                    continue;
                                }
                                x.roster.get(i).temp1.add(trying);
                                if (i == 0) {
                                    x.periodsTrying.add("" + tryString[a][0] + "" + tryString[a][1] + ""
                                            + tryString[a][2] + " " + tryString[b][0] + "" + tryString[b][1] + ""
                                            + tryString[b][2] + " " + tryString[c][0] + "" + tryString[c][1] + ""
                                            + tryString[c][2]);
                                    numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                                }

                                x.roster.get(i).conflict.add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                numPermsArray.get(count).add(Student.maxP);//
                                count++;
                                Student.maxP = 0;
                                x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for
                                // next round, not sure
                                // this is necessary
                            }
                }
                if (sect == 4) {
                    for (int a = 0; a < 25; a++)
                        for (int b = a + 1; b < triers.length - 2; b++)
                            for (int c = b + 1; c < triers.length - 1; c++)
                                for (int d = c + 1; d < triers.length; d++) {
                                    ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                    ArrayList<Integer> trying1 = new ArrayList<>();
                                    trying1.add(triers[a][0]);
                                    trying1.add(triers[a][1]);
                                    trying1.add(triers[a][2]);
                                    trying.add(trying1);
                                    ArrayList<Integer> trying2 = new ArrayList<>();
                                    trying2.add(triers[b][0]);
                                    trying2.add(triers[b][1]);
                                    trying2.add(triers[b][2]);
                                    trying.add(trying2);
                                    ArrayList<Integer> trying3 = new ArrayList<>();
                                    trying3.add(triers[c][0]);
                                    trying3.add(triers[c][1]);
                                    trying3.add(triers[c][2]);
                                    trying.add(trying3);
                                    ArrayList<Integer> trying4 = new ArrayList<>();
                                    trying4.add(triers[d][0]);
                                    trying4.add(triers[d][1]);
                                    trying4.add(triers[d][2]);
                                    trying.add(trying4);
                                    if (!Master.containsAll(x.periods, trying)) {
                                        continue;
                                    }

                                    x.roster.get(i).temp1.add(trying);
                                    if (i == 0) {
                                        x.periodsTrying.add("" + tryString[a][0] + "" + tryString[a][1] + ""
                                                + tryString[a][2] + " " + tryString[b][0] + "" + tryString[b][1] + ""
                                                + tryString[b][2] + " " + tryString[c][0] + "" + tryString[c][1] + ""
                                                + tryString[c][2] + " " + tryString[d][0] + "" + tryString[d][1] + ""
                                                + tryString[d][2]);
                                        numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                                    }

                                    x.roster.get(i).conflict
                                            .add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                    numPermsArray.get(count).add(Student.maxP);//
                                    count++;
                                    Student.maxP = 0;
                                    x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying for
                                    // next round, not
                                    // sure this is
                                    // necessary
                                }
                }
                if (sect == 5) {
                    for (int a = 0; a < triers.length - 4; a++)
                        for (int b = a + 1; b < triers.length - 3; b++)
                            for (int c = b + 1; c < triers.length - 2; c++)
                                for (int d = c + 1; d < triers.length - 1; d++)
                                    for (int e = d + 1; e < triers.length; e++) {
                                        ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                        ArrayList<Integer> trying1 = new ArrayList<>();
                                        trying1.add(triers[a][0]);
                                        trying1.add(triers[a][1]);
                                        trying1.add(triers[a][2]);
                                        trying.add(trying1);
                                        ArrayList<Integer> trying2 = new ArrayList<>();
                                        trying2.add(triers[b][0]);
                                        trying2.add(triers[b][1]);
                                        trying2.add(triers[b][2]);
                                        trying.add(trying2);
                                        ArrayList<Integer> trying3 = new ArrayList<>();
                                        trying3.add(triers[c][0]);
                                        trying3.add(triers[c][1]);
                                        trying3.add(triers[c][2]);
                                        trying.add(trying3);
                                        ArrayList<Integer> trying4 = new ArrayList<>();
                                        trying4.add(triers[d][0]);
                                        trying4.add(triers[d][1]);
                                        trying4.add(triers[d][2]);
                                        trying.add(trying4);
                                        ArrayList<Integer> trying5 = new ArrayList<>();
                                        trying5.add(triers[e][0]);
                                        trying5.add(triers[e][1]);
                                        trying5.add(triers[e][2]);
                                        trying.add(trying5);
                                        if (!Master.containsAll(x.periods, trying)) {
                                            continue;
                                        }
                                        x.roster.get(i).temp1.add(trying);
                                        if (i == 0) {
                                            x.periodsTrying.add("" + tryString[a][0] + "" + tryString[a][1] + ""
                                                    + tryString[a][2] + " " + tryString[b][0] + "" + tryString[b][1]
                                                    + "" + tryString[b][2] + " " + tryString[c][0] + ""
                                                    + tryString[c][1] + "" + tryString[c][2] + " " + tryString[d][0]
                                                    + "" + tryString[d][1] + "" + tryString[d][2] + " "
                                                    + tryString[e][0] + "" + tryString[e][1] + "" + tryString[e][2]);
                                            numPermsArray.add(new ArrayList<>());// not sure about this, think its good
                                        }

                                        x.roster.get(i).conflict
                                                .add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                        numPermsArray.get(count).add(Student.maxP);//
                                        count++;
                                        Student.maxP = 0;
                                        x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes trying
                                        // for next
                                        // round, not
                                        // sure this is
                                        // necessary
                                    }
                }

                if (sect == 6) {
                    for (int a = 0; a < triers.length - 4; a++)
                        for (int b = a + 1; b < triers.length - 3; b++)
                            for (int c = b + 1; c < triers.length - 2; c++)
                                for (int d = c + 1; d < triers.length - 1; d++)
                                    for (int e = d + 1; e < triers.length; e++)
                                        for (int f = e + 1; f < triers.length; f++) {
                                            ArrayList<ArrayList<Integer>> trying = new ArrayList<>();
                                            ArrayList<Integer> trying1 = new ArrayList<>();
                                            trying1.add(triers[a][0]);
                                            trying1.add(triers[a][1]);
                                            trying1.add(triers[a][2]);
                                            trying.add(trying1);
                                            ArrayList<Integer> trying2 = new ArrayList<>();
                                            trying2.add(triers[b][0]);
                                            trying2.add(triers[b][1]);
                                            trying2.add(triers[b][2]);
                                            trying.add(trying2);
                                            ArrayList<Integer> trying3 = new ArrayList<>();
                                            trying3.add(triers[c][0]);
                                            trying3.add(triers[c][1]);
                                            trying3.add(triers[c][2]);
                                            trying.add(trying3);
                                            ArrayList<Integer> trying4 = new ArrayList<>();
                                            trying4.add(triers[d][0]);
                                            trying4.add(triers[d][1]);
                                            trying4.add(triers[d][2]);
                                            trying.add(trying4);
                                            ArrayList<Integer> trying5 = new ArrayList<>();
                                            trying5.add(triers[e][0]);
                                            trying5.add(triers[e][1]);
                                            trying5.add(triers[e][2]);
                                            trying.add(trying5);
                                            ArrayList<Integer> trying6 = new ArrayList<>();
                                            trying6.add(triers[f][0]);
                                            trying6.add(triers[f][1]);
                                            trying6.add(triers[f][2]);
                                            trying.add(trying6);
                                            if (!Master.containsAll(x.periods, trying)) {
                                                continue;
                                            }
                                            x.roster.get(i).temp1.add(trying);
                                            if (i == 0) {
                                                x.periodsTrying.add("" + tryString[a][0] + "" + tryString[a][1] + ""
                                                        + tryString[a][2] + " " + tryString[b][0] + "" + tryString[b][1]
                                                        + "" + tryString[b][2] + " " + tryString[c][0] + ""
                                                        + tryString[c][1] + "" + tryString[c][2] + " " + tryString[d][0]
                                                        + "" + tryString[d][1] + "" + tryString[d][2] + " "
                                                        + tryString[e][0] + "" + tryString[e][1] + "" + tryString[e][2]
                                                        + " " + tryString[f][0] + "" + tryString[f][1] + ""
                                                        + tryString[f][2]);
                                                numPermsArray.add(new ArrayList<>());// not sure about this, think its
                                                // good
                                            }

                                            x.roster.get(i).conflict
                                                    .add(x.roster.get(i).before - take3(x.roster.get(i).temp1) == 0);
                                            numPermsArray.get(count).add(Student.maxP);//
                                            count++;
                                            Student.maxP = 0;
                                            x.roster.get(i).temp1.remove(x.roster.get(i).temp1.size() - 1);// removes
                                            // trying
                                            // for next
                                            // round,
                                            // not sure
                                            // this is
                                            // necessary
                                        }
                }

            }
        }
        // sorting numPermsArray below
        for (int i = 0; i < numPermsArray.size(); i++) {
            Collections.sort(numPermsArray.get(i));
        }

        boolean printAll = true;
        int minConflicts = 100, conflictCount = 0;
        for (int aa = 0; aa < 2; aa++) {
            for (int i = 0; i < x.periodsTrying.size(); i++)// for each possible arrangement of periods
            {
                int count = 0;
                for (int j = 0; j < x.roster.size(); j++)// for each student
                {
                    if (x.roster.get(j).conflict.get(i))
                        count++;
                }
                if (Math.min(count, minConflicts) != minConflicts) {
                    minConflicts = Math.min(count, minConflicts);
                    conflictCount = 1;
                } else if (count == minConflicts)
                    conflictCount++;
                if (printAll || count == minConflicts) {
                    System.out.print("Periods: " + x.periodsTrying.get(i) + " - ");
                    System.out.print(count + "  conflicts ");

                    // insert the numPermArrays stuff here
                    // numPermArray has to be sorted for loop below to work correctly, not true with
                    // j <=5,
                    // System.out.print(numPermsArray.get(i));

                    for (int j = 1; j <= 5; j++) {
                        int counter = 0;
                        for (int k = 0; k < numPermsArray.get(i).size(); k++) {
                            if (numPermsArray.get(i).get(k) == j)
                                counter++;
                        }
                        // if (counter!=0)
                        System.out.print(counter + " ");
                    }

                    for (int k = 0; k < x.roster.size(); k++)// for each student
                        if (x.roster.get(k).conflict.get(i))
                            System.out.print(" " + x.roster.get(k).name + " ");
                    System.out.println();
                }
            }
            if (conflictCount >= .75 * x.periodsTrying.size()) // TODO change limitation size here
                break; // won't print rest of data if enough of data is good

            if (printAll) {
                System.out.println("---------------------------------"); // just to separate data
            }

            printAll = false;
        }
        for (int j = 0; j < x.roster.size(); j++) { // taken out of aa loop
            x.roster.get(j).conflict.clear();
            // clears conflict ArrayList for next time, not sure necessary
        }
        x.periodsTrying.clear();
        // clears periodTrying for next time, not sure necessary
    }

    void populatePeriodArray(Course course) { //implement multiple sections?
        ArrayList<Integer> trySect = new ArrayList<Integer>();
        for (int i = 1; i <= 17 - course.lab * 2; i += course.lab * 2) {
            trySect.clear();
            for (int j = i; j < i + course.lab * 2; j++)
                trySect.add(j);

            course.periods.add((ArrayList<Integer>) trySect.clone()); // needs to be cloned as clearing trySect clears it inside the periods */
        }
    }

    void check(Course... testingCourses) { //working but not exactly as intended? needs checking
        ArrayList<ArrayList<Integer>> numPermsArray = new ArrayList<>();

        for (int i = 0; i < testingCourses.length; i++) { // can't use for each, need to change course outside loop
            if (testingCourses[i].periods.isEmpty())
                populatePeriodArray(testingCourses[i]);
//            for (Student student : testingCourses[i].roster) {
//                if (!currCourse.roster.contains(student))
//                    currCourse.roster.add(student);
//            }
        }
        for (Course currCourse : testingCourses) {
            int classCount = 0, period = 0; // used for numPermsArray, based on the period?
            for (int student = 0; student < currCourse.roster.size(); student++) { // change to regular for loop to access changed conflict boolean
                Student.maxP = 0;
                numPermsArray.add(new ArrayList<Integer>());
                if (currCourse.roster.get(student).temp1.isEmpty()) {
                    for (Course course : currCourse.roster.get(student).courses) // add student courses
                        currCourse.roster.get(student).temp1.add(course.periods);
                    currCourse.roster.get(student).before = take3(currCourse.roster.get(student).temp1);
                }

//                for (Course course : testingCourses) { // add testing courses -- check as if say testingCourse[!currCourse] is scheduled only for a certain period
//                    if (course != currCourse) // add all other courses to temp1
//                        currCourse.roster.get(student).temp1.add(course.periods);
//                }
//                for (ArrayList<Integer> section : currCourse.periods) {
//                    ArrayList<ArrayList<Integer>> course = new ArrayList<>();
//                    course.add(section); // needed to add this for temp1 to work
//                    currCourse.roster.get(student).temp1.add(course);
//                    currCourse.roster.get(student).conflict.add(currCourse.roster.get(student).before == take3(currCourse.roster.get(student).temp1));
//                    currCourse.roster.get(student).temp1.remove(currCourse.roster.get(student).temp1.size() - 1);
//                    numPermsArray.get(classCount).add(Student.maxP); //++classCount allows next use of classCount to be += 1
//                }
                recurseCheck(testingCourses, currCourse.roster.get(student));
                classCount++;
                currCourse.roster.get(student).temp1.clear();
            }

            boolean printAll = true;
            int minConflict = 100, numPeriodsConflicted = 0;
            for (int i = 0; i < numPermsArray.size(); i++) {
                Collections.sort(numPermsArray.get(i));
            }
            for (int runs = 0; runs < 2; runs++) {
                System.out.println(currCourse.name);
                for (ArrayList<Integer> periods : currCourse.periods) {
                    int conflictCount = 0;
                    for (Student student : currCourse.roster) {
                        if (student.conflict.get(period))
                            conflictCount++;
                    }
                    if (Math.min(minConflict, conflictCount) != minConflict) {
                        minConflict = conflictCount;
                        numPeriodsConflicted = 1;
                    } else if (conflictCount == minConflict)
                        numPeriodsConflicted++;

                    if (printAll || conflictCount == minConflict) {
                        System.out.print("Period: " + periods + " - " + conflictCount + " conflicts ");

                        int index = currCourse.periods.indexOf(periods);
                        for (int j = 1; j <= 5; j++) {
                            int counter = 0;
                            for (int k = 0; k < numPermsArray.get(index).size(); k++) {
                                if (numPermsArray.get(index).get(k) == j)
                                    counter++;
                            }
                            // if (counter!=0)
                            System.out.print(counter + " ");
                        }
                        for (Student student : currCourse.roster) {
                            if (student.conflict.get(period))
                                System.out.print(" " + student.name);
                        }
                        System.out.println();
                    }
                    period++;
                }

                if (numPeriodsConflicted >= .75 * currCourse.periods.size())
                    break;

                if (printAll)
                    System.out.println("-------------------------------");
                printAll = false;
            }
        }

    }

    void recurseCheck(Course[] courses, Student student) {
        Course course = courses[0];
        for (ArrayList<Integer> section : course.periods) {
            ArrayList<ArrayList<Integer>> coursePeriods = new ArrayList<>();
            coursePeriods.add(section); // needed to add this for temp1 to work
            student.temp1.add(coursePeriods);
            if (courses.length == 1)
                student.conflict.add(student.before == take3(student.temp1));
            else {
                Course[] newCourses = new Course[courses.length - 1];
                for(int i = 1; i < courses.length; i++){
                    newCourses[i - 1] = courses[i];
                }
                recurseCheck(newCourses, student);
            }
            student.temp1.remove(student.temp1.size() - 1);
        }
    }
}


class Course {
    String name;
    String number;
    ArrayList<Student> roster;
    ArrayList<ArrayList<Integer>> periods;
    ArrayList<String> periodsTrying;
    ArrayList<Student> conflictedStudents;
    ArrayList<ArrayList<Integer>> seatedPeriods;

    int numSecs;
    double lab;
    boolean posted = false;
    int timesConflicted = 0;
    int numStudentsConflicted = 0;
    int numConflictGroups = 0;
    boolean consec = true;

    public Course() {

    }

    public Course(String name, String number, int numSecs) {
        this.name = name;
        this.numSecs = numSecs;
        this.number = number;
        roster = new ArrayList<>();
        periods = new ArrayList<>();
        periodsTrying = new ArrayList<>();
        seatedPeriods = new ArrayList<>();
        lab = 1;
        conflictedStudents = new ArrayList<>();
    }

    public Course(String name, String number, int numSecs, double lab)// lab = 1, 1.5, or .5
    {
        this(name, number, numSecs);
        this.lab = lab;
    }

    public void add(Student... x) {
        roster.addAll(new ArrayList<>(Arrays.asList(x)));

    }

    public String toString() {
        return name;
    }

    public void seatAdd(Integer... period) {
        ArrayList empty = new ArrayList<>();
        seatedPeriods.add(new ArrayList<Integer>(Arrays.asList(period))); // Exception in thread "main"
        // java.lang.IndexOutOfBoundsException:
        // Index 0 out of bounds for length 0
    }

}

class Student implements Comparable<Student> {
    static int maxP;// max number of permutations, just a holder
    int maxPerm;// max number of permutations
    String name;
    int grade;
    int able;
    int dif;
    ArrayList<Course> courses;
    ArrayList<ArrayList<ArrayList<Integer>>> temp1;
    ArrayList<ArrayList<ArrayList<Integer>>> temp2;
    ArrayList<ArrayList<Integer>> temp3;
    ArrayList<Boolean> conflict;
    int numConflicts = 0;
    ArrayList<ArrayList<Course>> stuConflicts = new ArrayList<>();

    int before = 0;

    public int compareTo(Student x) {

        return name.compareTo(x.name);
    }

    public Student(String name, int grade) {
        this(name);
        this.grade = grade;

    }

    public Student(String name) {
        this.name = name;
        courses = new ArrayList<>();
        temp1 = new ArrayList<>();
        temp2 = new ArrayList<>();
        temp3 = new ArrayList<>();
        conflict = new ArrayList<>();

    }

    public String toString() {
        return name;
    }
}

public class Tester {
    public static void main(String[] args) {

        Master x = new Master();
        //
        Student Stu1 = new Student("Stu1", 10);
        Student Stu2 = new Student("Stu2", 11);
        Student Stu3 = new Student("Stu3", 9);
        Student Stu4 = new Student("Stu4", 12);
        Student Stu5 = new Student("Stu5", 11);
        Student Stu6 = new Student("Stu6", 11);
        Student Stu7 = new Student("Stu7", 9);
        Student Stu8 = new Student("Stu8", 9);
        Student Stu9 = new Student("Stu9", 9);
        Student Stu10 = new Student("Stu10", 12);
        Student Stu11 = new Student("Stu11", 9);
        Student Stu12 = new Student("Stu12", 10);
        Student Stu13 = new Student("Stu13", 9);
        Student Stu14 = new Student("Stu14", 12);
        Student Stu15 = new Student("Stu15", 9);
        Student Stu16 = new Student("Stu16", 11);
        Student Stu17 = new Student("Stu17", 12);
        Student Stu18 = new Student("Stu18", 9);
        Student Stu19 = new Student("Stu19", 9);
        Student Stu20 = new Student("Stu20", 10);
        Student Stu21 = new Student("Stu21", 9);
        Student Stu22 = new Student("Stu22", 12);
        Student Stu23 = new Student("Stu23", 10);
        Student Stu24 = new Student("Stu24", 12);
        Student Stu25 = new Student("Stu25", 11);
        Student Stu26 = new Student("Stu26", 9);
        Student Stu27 = new Student("Stu27", 12);
        Student Stu28 = new Student("Stu28", 10);
        Student Stu29 = new Student("Stu29", 11);
        Student Stu30 = new Student("Stu30", 10);
        Student Stu31 = new Student("Stu31", 11);
        Student Stu32 = new Student("Stu32", 11);
        Student Stu33 = new Student("Stu33", 11);
        Student Stu34 = new Student("Stu34", 10);
        Student Stu35 = new Student("Stu35", 12);
        Student Stu36 = new Student("Stu36", 9);
        Student Stu37 = new Student("Stu37", 12);
        Student Stu38 = new Student("Stu38", 11);
        Student Stu39 = new Student("Stu39", 11);
        Student Stu40 = new Student("Stu40", 11);
        Student Stu41 = new Student("Stu41", 10);
        Student Stu42 = new Student("Stu42", 12);
        Student Stu43 = new Student("Stu43", 10);
        Student Stu44 = new Student("Stu44", 10);
        Student Stu45 = new Student("Stu45", 9);
        Student Stu46 = new Student("Stu46", 11);
        Student Stu47 = new Student("Stu47", 12);
        Student Stu48 = new Student("Stu48", 9);
        Student Stu49 = new Student("Stu49", 11);
        Student Stu50 = new Student("Stu50", 10);
        Student Stu51 = new Student("Stu51", 9);
        Student Stu52 = new Student("Stu52", 12);
        Student Stu53 = new Student("Stu53", 10);
        Student Stu54 = new Student("Stu54", 10);
        Student Stu55 = new Student("Stu55", 11);
        Student Stu56 = new Student("Stu56", 10);
        Student Stu57 = new Student("Stu57", 9);
        Student Stu58 = new Student("Stu58", 9);
        Student Stu59 = new Student("Stu59", 11);
        Student Stu60 = new Student("Stu60", 10);
        Student Stu61 = new Student("Stu61", 10);
        Student Stu62 = new Student("Stu62", 9);
        Student Stu63 = new Student("Stu63", 10);
        Student Stu64 = new Student("Stu64", 12);
        Student Stu65 = new Student("Stu65", 9);
        Student Stu66 = new Student("Stu66", 10);
        Student Stu67 = new Student("Stu67", 9);
        Student Stu68 = new Student("Stu68", 12);
        Student Stu69 = new Student("Stu69", 10);
        Student Stu70 = new Student("Stu70", 12);
        Student Stu71 = new Student("Stu71", 12);
        Student Stu72 = new Student("Stu72", 12);
        Student Stu73 = new Student("Stu73", 11);
        Student Stu74 = new Student("Stu74", 9);
        Student Stu75 = new Student("Stu75", 11);
        Student Stu76 = new Student("Stu76", 9);
        Student Stu77 = new Student("Stu77", 9);
        Student Stu78 = new Student("Stu78", 11);
        Student Stu79 = new Student("Stu79", 11);
        Student Stu80 = new Student("Stu80", 9);
        Student Stu81 = new Student("Stu81", 9);
        Student Stu82 = new Student("Stu82", 12);
        Student Stu83 = new Student("Stu83", 9);
        Student Stu84 = new Student("Stu84", 9);
        Student Stu85 = new Student("Stu85", 11);
        Student Stu86 = new Student("Stu86", 11);
        Student Stu87 = new Student("Stu87", 12);
        Student Stu88 = new Student("Stu88", 12);
        Student Stu89 = new Student("Stu89", 12);
        Student Stu90 = new Student("Stu90", 10);
        Student Stu91 = new Student("Stu91", 11);
        Student Stu92 = new Student("Stu92", 11);
        Student Stu93 = new Student("Stu93", 9);
        Student Stu94 = new Student("Stu94", 12);
        Student Stu95 = new Student("Stu95", 12);
        Student Stu96 = new Student("Stu96", 12);
        Student Stu97 = new Student("Stu97", 9);
        Student Stu98 = new Student("Stu98", 11);
        Student Stu99 = new Student("Stu99", 12);
        Student Stu100 = new Student("Stu100", 9);
        Student Stu101 = new Student("Stu101", 12);
        Student Stu102 = new Student("Stu102", 10);
        Student Stu103 = new Student("Stu103", 12);
        Student Stu104 = new Student("Stu104", 9);
        Student Stu105 = new Student("Stu105", 9);
        Student Stu106 = new Student("Stu106", 11);
        Student Stu107 = new Student("Stu107", 9);
        Student Stu108 = new Student("Stu108", 10);
        Student Stu109 = new Student("Stu109", 10);
        Student Stu110 = new Student("Stu110", 12);
        Student Stu111 = new Student("Stu111", 10);
        Student Stu112 = new Student("Stu112", 9);
        Student Stu113 = new Student("Stu113", 12);
        Student Stu114 = new Student("Stu114", 10);
        Student Stu115 = new Student("Stu115", 12);
        Student Stu116 = new Student("Stu116", 12);
        Student Stu117 = new Student("Stu117", 11);
        Student Stu118 = new Student("Stu118", 12);
        Student Stu119 = new Student("Stu119", 9);
        Student Stu120 = new Student("Stu120", 11);
        Student Stu121 = new Student("Stu121", 9);
        Student Stu122 = new Student("Stu122", 11);
        Student Stu123 = new Student("Stu123", 9);
        Student Stu124 = new Student("Stu124", 9);
        Student Stu125 = new Student("Stu125", 11);
        Student Stu126 = new Student("Stu126", 9);
        Student Stu127 = new Student("Stu127", 9);
        Student Stu128 = new Student("Stu128", 9);
        Student Stu129 = new Student("Stu129", 11);
        Student Stu130 = new Student("Stu130", 11);
        Student Stu131 = new Student("Stu131", 9);
        Student Stu132 = new Student("Stu132", 9);
        Student Stu133 = new Student("Stu133", 11);
        Student Stu134 = new Student("Stu134", 9);
        Student Stu135 = new Student("Stu135", 11);
        Student Stu136 = new Student("Stu136", 12);
        Student Stu137 = new Student("Stu137", 11);
        Student Stu138 = new Student("Stu138", 12);
        Student Stu139 = new Student("Stu139", 9);
        Student Stu140 = new Student("Stu140", 10);
        Student Stu141 = new Student("Stu141", 9);
        Student Stu142 = new Student("Stu142", 12);
        Student Stu143 = new Student("Stu143", 12);
        Student Stu144 = new Student("Stu144", 9);
        Student Stu145 = new Student("Stu145", 12);
        Student Stu146 = new Student("Stu146", 9);
        Student Stu147 = new Student("Stu147", 12);
        Student Stu148 = new Student("Stu148", 12);
        Student Stu149 = new Student("Stu149", 11);
        Student Stu150 = new Student("Stu150", 11);
        Student Stu151 = new Student("Stu151", 9);
        Student Stu152 = new Student("Stu152", 11);
        Student Stu153 = new Student("Stu153", 12);
        Student Stu154 = new Student("Stu154", 9);
        Student Stu155 = new Student("Stu155", 11);
        Student Stu156 = new Student("Stu156", 9);
        Student Stu157 = new Student("Stu157", 12);
        Student Stu158 = new Student("Stu158", 9);
        Student Stu159 = new Student("Stu159", 9);
        Student Stu160 = new Student("Stu160", 11);
        Student Stu161 = new Student("Stu161", 10);
        Student Stu162 = new Student("Stu162", 10);
        Student Stu163 = new Student("Stu163", 10);
        Student Stu164 = new Student("Stu164", 10);
        Student Stu165 = new Student("Stu165", 12);
        Student Stu166 = new Student("Stu166", 10);
        Student Stu167 = new Student("Stu167", 9);
        Student Stu168 = new Student("Stu168", 10);
        Student Stu169 = new Student("Stu169", 9);
        Student Stu170 = new Student("Stu170", 9);
        Student Stu171 = new Student("Stu171", 12);
        Student Stu172 = new Student("Stu172", 11);
        Student Stu173 = new Student("Stu173", 12);
        Student Stu174 = new Student("Stu174", 10);
        Student Stu175 = new Student("Stu175", 11);
        Student Stu176 = new Student("Stu176", 12);
        Student Stu177 = new Student("Stu177", 10);
        Student Stu178 = new Student("Stu178", 11);
        Student Stu179 = new Student("Stu179", 10);
        Student Stu180 = new Student("Stu180", 12);
        Student Stu181 = new Student("Stu181", 10);
        Student Stu182 = new Student("Stu182", 9);
        Student Stu183 = new Student("Stu183", 12);
        Student Stu184 = new Student("Stu184", 12);
        Student Stu185 = new Student("Stu185", 11);
        Student Stu186 = new Student("Stu186", 12);
        Student Stu187 = new Student("Stu187", 11);
        Student Stu188 = new Student("Stu188", 11);
        Student Stu189 = new Student("Stu189", 10);
        Student Stu190 = new Student("Stu190", 11);
        Student Stu191 = new Student("Stu191", 10);
        Student Stu192 = new Student("Stu192", 9);
        Student Stu193 = new Student("Stu193", 11);
        Student Stu194 = new Student("Stu194", 12);
        Student Stu195 = new Student("Stu195", 9);
        Student Stu196 = new Student("Stu196", 10);
        Student Stu197 = new Student("Stu197", 12);
        Student Stu198 = new Student("Stu198", 10);
        Student Stu199 = new Student("Stu199", 11);
        Student Stu200 = new Student("Stu200", 11);
        Student Stu201 = new Student("Stu201", 11);
        Student Stu202 = new Student("Stu202", 11);
        Student Stu203 = new Student("Stu203", 10);
        Student Stu204 = new Student("Stu204", 9);
        Student Stu205 = new Student("Stu205", 10);
        Student Stu206 = new Student("Stu206", 12);
        Student Stu207 = new Student("Stu207", 11);
        Student Stu208 = new Student("Stu208", 12);
        Student Stu209 = new Student("Stu209", 11);
        Student Stu210 = new Student("Stu210", 10);
        Student Stu211 = new Student("Stu211", 9);
        Student Stu212 = new Student("Stu212", 11);
        Student Stu213 = new Student("Stu213", 11);
        Student Stu214 = new Student("Stu214", 9);
        Student Stu215 = new Student("Stu215", 10);
        Student Stu216 = new Student("Stu216", 11);
        Student Stu217 = new Student("Stu217", 9);
        Student Stu218 = new Student("Stu218", 11);
        Student Stu219 = new Student("Stu219", 9);
        Student Stu220 = new Student("Stu220", 12);
        Student Stu221 = new Student("Stu221", 11);
        Student Stu222 = new Student("Stu222", 9);
        Student Stu223 = new Student("Stu223", 9);
        Student Stu224 = new Student("Stu224", 12);
        Student Stu225 = new Student("Stu225", 9);
        Student Stu226 = new Student("Stu226", 12);
        Student Stu227 = new Student("Stu227", 11);
        Student Stu228 = new Student("Stu228", 9);
        Student Stu229 = new Student("Stu229", 11);
        Student Stu230 = new Student("Stu230", 11);
        Student Stu231 = new Student("Stu231", 10);
        Student Stu232 = new Student("Stu232", 9);
        Student Stu233 = new Student("Stu233", 10);
        Student Stu234 = new Student("Stu234", 9);
        Student Stu235 = new Student("Stu235", 11);
        Student Stu236 = new Student("Stu236", 10);
        Student Stu237 = new Student("Stu237", 11);
        Student Stu238 = new Student("Stu238", 10);
        Student Stu239 = new Student("Stu239", 10);
        Student Stu240 = new Student("Stu240", 11);
        Student Stu241 = new Student("Stu241", 12);
        Student Stu242 = new Student("Stu242", 12);
        Student Stu243 = new Student("Stu243", 11);
        Student Stu244 = new Student("Stu244", 12);
        Student Stu245 = new Student("Stu245", 9);
        Student Stu246 = new Student("Stu246", 11);
        Student Stu247 = new Student("Stu247", 11);
        Student Stu248 = new Student("Stu248", 10);
        Student Stu249 = new Student("Stu249", 11);
        Student Stu250 = new Student("Stu250", 11);
        Student Stu251 = new Student("Stu251", 11);
        Student Stu252 = new Student("Stu252", 11);
        Student Stu253 = new Student("Stu253", 11);
        Student Stu254 = new Student("Stu254", 11);
        Student Stu255 = new Student("Stu255", 9);
        Student Stu256 = new Student("Stu256", 11);
        Student Stu257 = new Student("Stu257", 9);
        Student Stu258 = new Student("Stu258", 10);
        Student Stu259 = new Student("Stu259", 12);
        Student Stu260 = new Student("Stu260", 10);
        Student Stu261 = new Student("Stu261", 10);
        Student Stu262 = new Student("Stu262", 9);
        Student Stu263 = new Student("Stu263", 12);
        Student Stu264 = new Student("Stu264", 12);
        Student Stu265 = new Student("Stu265", 12);
        Student Stu266 = new Student("Stu266", 10);
        Student Stu267 = new Student("Stu267", 12);
        Student Stu268 = new Student("Stu268", 11);
        Student Stu269 = new Student("Stu269", 9);
        Student Stu270 = new Student("Stu270", 12);
        Student Stu271 = new Student("Stu271", 9);
        Student Stu272 = new Student("Stu272", 12);
        Student Stu273 = new Student("Stu273", 9);
        Student Stu274 = new Student("Stu274", 10);
        Student Stu275 = new Student("Stu275", 9);
        Student Stu276 = new Student("Stu276", 12);
        Student Stu277 = new Student("Stu277", 11);
        Student Stu278 = new Student("Stu278", 12);
        Student Stu279 = new Student("Stu279", 11);
        Student Stu280 = new Student("Stu280", 12);
        Student Stu281 = new Student("Stu281", 10);
        Student Stu282 = new Student("Stu282", 11);
        Student Stu283 = new Student("Stu283", 11);
        Student Stu284 = new Student("Stu284", 12);
        Student Stu285 = new Student("Stu285", 11);
        Student Stu286 = new Student("Stu286", 11);
        Student Stu287 = new Student("Stu287", 9);
        Student Stu288 = new Student("Stu288", 9);
        Student Stu289 = new Student("Stu289", 11);
        Student Stu290 = new Student("Stu290", 12);
        Student Stu291 = new Student("Stu291", 11);
        Student Stu292 = new Student("Stu292", 9);
        Student Stu293 = new Student("Stu293", 10);
        Student Stu294 = new Student("Stu294", 10);
        Student Stu295 = new Student("Stu295", 10);
        Student Stu296 = new Student("Stu296", 12);
        Student Stu297 = new Student("Stu297", 9);
        Student Stu298 = new Student("Stu298", 10);
        Student Stu299 = new Student("Stu299", 11);
        Student Stu300 = new Student("Stu300", 12);
        Student Stu301 = new Student("Stu301", 12);
        Student Stu302 = new Student("Stu302", 12);
        Student Stu303 = new Student("Stu303", 11);
        Student Stu304 = new Student("Stu304", 10);
        Student Stu305 = new Student("Stu305", 11);
        Student Stu306 = new Student("Stu306", 10);
        Student Stu307 = new Student("Stu307", 9);
        Student Stu308 = new Student("Stu308", 12);
        Student Stu309 = new Student("Stu309", 10);
        Student Stu310 = new Student("Stu310", 12);
        Student Stu311 = new Student("Stu311", 9);
        Student Stu312 = new Student("Stu312", 10);
        Student Stu313 = new Student("Stu313", 12);
        Student Stu314 = new Student("Stu314", 12);
        Student Stu315 = new Student("Stu315", 12);
        Student Stu316 = new Student("Stu316", 9);
        Student Stu317 = new Student("Stu317", 11);
        Student Stu318 = new Student("Stu318", 9);
        Student Stu319 = new Student("Stu319", 10);
        Student Stu320 = new Student("Stu320", 12);
        Student Stu321 = new Student("Stu321", 10);
        Student Stu322 = new Student("Stu322", 12);
        Student Stu323 = new Student("Stu323", 9);
        Student Stu324 = new Student("Stu324", 10);
        Student Stu325 = new Student("Stu325", 11);
        Student Stu326 = new Student("Stu326", 9);
        Student Stu327 = new Student("Stu327", 12);
        Student Stu328 = new Student("Stu328", 10);
        Student Stu329 = new Student("Stu329", 11);
        Student Stu330 = new Student("Stu330", 12);
        Student Stu331 = new Student("Stu331", 12);
        Student Stu332 = new Student("Stu332", 12);
        Student Stu333 = new Student("Stu333", 9);
        Student Stu334 = new Student("Stu334", 9);
        Student Stu335 = new Student("Stu335", 10);
        Student Stu336 = new Student("Stu336", 10);
        Student Stu337 = new Student("Stu337", 12);
        Student Stu338 = new Student("Stu338", 12);
        Student Stu339 = new Student("Stu339", 11);
        Student Stu340 = new Student("Stu340", 12);
        Student Stu341 = new Student("Stu341", 9);
        Student Stu342 = new Student("Stu342", 9);
        Student Stu343 = new Student("Stu343", 10);
        Student Stu344 = new Student("Stu344", 12);
        Student Stu345 = new Student("Stu345", 11);
        Student Stu346 = new Student("Stu346", 10);
        Student Stu347 = new Student("Stu347", 11);
        Student Stu348 = new Student("Stu348", 9);
        Student Stu349 = new Student("Stu349", 9);
        Student Stu350 = new Student("Stu350", 12);
        Student Stu351 = new Student("Stu351", 11);
        Student Stu352 = new Student("Stu352", 11);
        Student Stu353 = new Student("Stu353", 11);
        Student Stu354 = new Student("Stu354", 11);
        Student Stu355 = new Student("Stu355", 11);
        Student Stu356 = new Student("Stu356", 12);
        Student Stu357 = new Student("Stu357", 9);
        Student Stu358 = new Student("Stu358", 12);
        Student Stu359 = new Student("Stu359", 12);
        Student Stu360 = new Student("Stu360", 10);
        Student Stu361 = new Student("Stu361", 9);
        Student Stu362 = new Student("Stu362", 11);
        Student Stu363 = new Student("Stu363", 12);
        Student Stu364 = new Student("Stu364", 10);
        Student Stu365 = new Student("Stu365", 12);
        Student Stu366 = new Student("Stu366", 12);
        Student Stu367 = new Student("Stu367", 11);
        Student Stu368 = new Student("Stu368", 10);
        Student Stu369 = new Student("Stu369", 12);
        Student Stu370 = new Student("Stu370", 11);
        Student Stu371 = new Student("Stu371", 11);
        Student Stu372 = new Student("Stu372", 12);
        Student Stu373 = new Student("Stu373", 12);
        Student Stu374 = new Student("Stu374", 11);
        Student Stu375 = new Student("Stu375", 12);
        Student Stu376 = new Student("Stu376", 9);
        Student Stu377 = new Student("Stu377", 12);
        Student Stu378 = new Student("Stu378", 10);
        Student Stu379 = new Student("Stu379", 9);
        Student Stu380 = new Student("Stu380", 10);
        Student Stu381 = new Student("Stu381", 9);
        Student Stu382 = new Student("Stu382", 9);
        Student Stu383 = new Student("Stu383", 10);
        Student Stu384 = new Student("Stu384", 9);
        Student Stu385 = new Student("Stu385", 10);
        Student Stu386 = new Student("Stu386", 12);
        Student Stu387 = new Student("Stu387", 12);
        Student Stu388 = new Student("Stu388", 10);
        Student Stu389 = new Student("Stu389", 12);
        Student Stu390 = new Student("Stu390", 10);
        Student Stu391 = new Student("Stu391", 9);
        Student Stu392 = new Student("Stu392", 10);
        Student Stu393 = new Student("Stu393", 12);
        Student Stu394 = new Student("Stu394", 11);
        Student Stu395 = new Student("Stu395", 10);
        Student Stu396 = new Student("Stu396", 10);
        Student Stu397 = new Student("Stu397", 10);
        Student Stu398 = new Student("Stu398", 9);
        Student Stu399 = new Student("Stu399", 9);
        Student Stu400 = new Student("Stu400", 10);
        Student Stu401 = new Student("Stu401", 11);
        Student Stu402 = new Student("Stu402", 12);
        Student Stu403 = new Student("Stu403", 11);
        Student Stu404 = new Student("Stu404", 11);
        Student Stu405 = new Student("Stu405", 12);
        Student Stu406 = new Student("Stu406", 9);
        Student Stu407 = new Student("Stu407", 9);
        Student Stu408 = new Student("Stu408", 9);
        Student Stu409 = new Student("Stu409", 11);
        Student Stu410 = new Student("Stu410", 12);
        Student Stu411 = new Student("Stu411", 9);
        Student Stu412 = new Student("Stu412", 9);
        Student Stu413 = new Student("Stu413", 11);
        Student Stu414 = new Student("Stu414", 10);
        Student Stu415 = new Student("Stu415", 10);
        Student Stu416 = new Student("Stu416", 12);
        Student Stu417 = new Student("Stu417", 12);
        Student Stu418 = new Student("Stu418", 10);
        Student Stu419 = new Student("Stu419", 11);
        Student Stu420 = new Student("Stu420", 12);
        Student Stu421 = new Student("Stu421", 10);
        Student Stu422 = new Student("Stu422", 10);
        Student Stu423 = new Student("Stu423", 11);
        Student Stu424 = new Student("Stu424", 11);
        Student Stu425 = new Student("Stu425", 10);
        Student Stu426 = new Student("Stu426", 9);
        Student Stu427 = new Student("Stu427", 9);
        Student Stu428 = new Student("Stu428", 10);
        Student Stu429 = new Student("Stu429", 12);
        Student Stu430 = new Student("Stu430", 12);
        Student Stu431 = new Student("Stu431", 10);
        Student Stu432 = new Student("Stu432", 12);
        Student Stu433 = new Student("Stu433", 12);
        Student Stu434 = new Student("Stu434", 9);
        Student Stu435 = new Student("Stu435", 12);
        Student Stu436 = new Student("Stu436", 11);
        Student Stu437 = new Student("Stu437", 11);
        Student Stu438 = new Student("Stu438", 11);
        Student Stu439 = new Student("Stu439", 11);
        Student Stu440 = new Student("Stu440", 9);
        Student Stu441 = new Student("Stu441", 9);
        Student Stu442 = new Student("Stu442", 12);
        Student Stu443 = new Student("Stu443", 9);
        Student Stu444 = new Student("Stu444", 9);
        Student Stu445 = new Student("Stu445", 10);
        Student Stu446 = new Student("Stu446", 12);
        Student Stu447 = new Student("Stu447", 12);
        Student Stu448 = new Student("Stu448", 12);
        Student Stu449 = new Student("Stu449", 12);
        Student Stu450 = new Student("Stu450", 9);
        Student Stu451 = new Student("Stu451", 9);
        Student Stu452 = new Student("Stu452", 11);
        Student Stu453 = new Student("Stu453", 10);
        Student Stu454 = new Student("Stu454", 11);
        Student Stu455 = new Student("Stu455", 9);
        Student Stu456 = new Student("Stu456", 11);
        Student Stu457 = new Student("Stu457", 11);
        Student Stu458 = new Student("Stu458", 9);
        Student Stu459 = new Student("Stu459", 10);
        Student Stu460 = new Student("Stu460", 11);
        Student Stu461 = new Student("Stu461", 9);
        Student Stu462 = new Student("Stu462", 10);
        Student Stu463 = new Student("Stu463", 11);
        Student Stu464 = new Student("Stu464", 9);
        Student Stu465 = new Student("Stu465", 11);
        Student Stu466 = new Student("Stu466", 9);
        Student Stu467 = new Student("Stu467", 11);
        Student Stu468 = new Student("Stu468", 10);
        Student Stu469 = new Student("Stu469", 12);
        Student Stu470 = new Student("Stu470", 10);
        Student Stu471 = new Student("Stu471", 9);


        Course Accounting_711010 = new Course("Accounting", "711010", 2);
        Course AdvancedArt_611040 = new Course("AdvancedArt", "611040", 1);
        Course AdvancedPhotoPortfolio_611130 = new Course("AdvancedPhotoPortfolio", "611130", 1);
        Course AlgebraI_201020 = new Course("AlgebraI", "201020", 2);
        Course AlgebraIITrigonometry_201080 = new Course("AlgebraIITrigonometry", "201080", 2);
        Course AlgebraIYr1_201021 = new Course("AlgebraIYr1", "201021", 1);
        Course AlgebraIYr2_201210 = new Course("AlgebraIYr2", "201210", 1);
        Course AlgebrawLab_201010 = new Course("AlgebrawLab", "201010", 1, 1.5);
        Course AlgebrawLab_201010I = new Course("AlgebrawLabI", "201010I", 1, 1.5);
        Course AP2DDesign_619110 = new Course("AP2DDesign", "619110", 1);
        Course APBiologywlab_PH309020 = new Course("APBiologywlab", "PH309020", 2, 1.5);
        Course APCalculusAB_209130 = new Course("APCalculusAB", "209130", 2);
        Course APCalculusBC_209140 = new Course("APCalculusBC", "209140", 1);
        Course APComputerScience_729010 = new Course("APComputerScience", "729010", 1);
        Course APDrawing_Painting_619060 = new Course("APDrawing_Painting", "619060", 1);
        Course APEnglish11LangandComp_109060 = new Course("APEnglish11LangandComp", "109060", 3);
        Course APEnglish12LitandComp_109050 = new Course("APEnglish12LitandComp", "109050", 2);
        Course APGovt_Politics_409100 = new Course("APGovt_Politics", "409100", 1);
        Course APItalianLanguage_Culture_509190 = new Course("APItalianLanguage_Culture", "509190", 1);
        Course APMacroeconomics_409090 = new Course("APMacroeconomics", "409090", 2);
        Course APPhysics1wlab_PH309030 = new Course("APPhysics1wlab", "PH309030", 2, 1.5);
        Course AppliedChemistry_301040 = new Course("AppliedChemistry", "301040", 1);
        Course APPsychology_309120 = new Course("APPsychology", "309120", 3);
        Course APPsychology_409120 = new Course("APPsychology", "409120", 3);
        Course APSpanishLanguage_Culture_509140 = new Course("APSpanishLanguage_Culture", "509140", 2);
        Course APStatistics_209120 = new Course("APStatistics", "209120", 1);
        Course APUSHistory_409040 = new Course("APUSHistory", "409040", 3);
        Course BiologywithLAB_PH301010 = new Course("BiologywithLAB", "PH301010", 6, 1.5);
        Course BiologywithLab_PH30101I = new Course("BiologywithLabI", "PH30101I", 1, 1.5);
        Course Calculus_201120 = new Course("Calculus", "201120", 1);
        Course Chemistrywlab_PH301050 = new Course("Chemistrywlab", "PH301050", 3, 1.5);
        Course Chorus_621010 = new Course("Chorus", "621010", 1);
        Course ChorusABDay_621013 = new Course("ChorusABDay", "621013", 1, .5);
        Course CollegeAccounting_717020 = new Course("CollegeAccounting", "717020", 2);
        Course CollegeChorus_627010 = new Course("CollegeChorus", "627010", 1);
        Course CollegeMarketing_717030 = new Course("CollegeMarketing", "717030", 2);
        Course CollegeWindEnsemble_637010 = new Course("CollegeWindEnsemble", "637010", 1);
        Course ComparativeLiterature_101050 = new Course("ComparativeLiterature", "101050", 3);
        Course ComparativeLiterature_101050I = new Course("ComparativeLiteratureI", "101050I", 1);
        Course ComparativeLiterature2_101050i2 = new Course("ComparativeLiterature2", "101050i2", 1);
        Course ConcertBand_631010 = new Course("ConcertBand", "631010", 1);
        Course ConcertBand_631013 = new Course("ConcertBand", "631013", 1, .5);
        Course ConstitutionalLaw_401083 = new Course("ConstitutionalLaw", "401083", 3, 0.5);
        Course ConstitutionalLaw_401083I = new Course("ConstitutionalLawI", "401083I", 3, 0.5);
        Course ContemporaryIssues_401093 = new Course("ContemporaryIssues", "401093", 2, 0.5);
        Course DigitalPhotography_611093 = new Course("DigitalPhotography", "611093", 2, 0.5);
        Course EarthSciencewlab_PH301150 = new Course("EarthSciencewlab", "PH301150", 1, 1.5);
        Course Economics_401053 = new Course("Economics", "401053", 3, 0.5);
        Course Economics_401053I = new Course("EconomicsI", "401053I", 1, 0.5);
        Course English10_101030 = new Course("English10", "101030", 5);
        Course English10_101030I = new Course("English10I", "101030I", 1);
        Course English10_101030S = new Course("English10S", "101030S", 1);
        Course English11_101040 = new Course("English11", "101040", 3);
        Course English11_101040I = new Course("English11I", "101040I", 1);
        Course English9_101010 = new Course("English9", "101010", 6, 1.5);
        Course English9_101010I = new Course("English9I", "101010I", 1, 1.5);
        Course English9_101010S = new Course("English9S", "101010S", 1);
        Course ESL_101011 = new Course("ESL", "101011", 1);
        Course ForensicScience_301130 = new Course("ForensicScience", "301130", 2);
        Course ForensicScience_301130i = new Course("ForensicScienceI", "301130i", 1);
        Course Geometry_201050 = new Course("Geometry", "201050", 2);
        Course GeometryB_201040 = new Course("GeometryB", "201040", 1);
        Course GlobalHistory_Geography10_401020 = new Course("GlobalHistory_Geography10", "401020", 5);
        Course GlobalHistory_Geography10_401020I = new Course("GlobalHistory_Geography10I", "401020I", 1);
        Course GlobalHistory_Geography9_401010 = new Course("GlobalHistory_Geography9", "401010", 6);
        Course GlobalHistory_Geography9_401010I = new Course("GlobalHistory_Geography9I", "401010I", 1);
        Course Health_881063 = new Course("Health", "881063", 5, 0.5);
        Course HonorsAlgebraIITrigonometry_208090 = new Course("HonorsAlgebraIITrigonometry", "208090", 3);
        Course HonorsChemistrywlab_PH308060 = new Course("HonorsChemistrywlab", "PH308060", 2, 1.5);
        Course HonorsComputerScience_728010 = new Course("HonorsComputerScience", "728010", 2);
        Course HonorsEngineeringDesign_728020 = new Course("HonorsEngineeringDesign", "728020", 1);
        Course HonorsGeometry_208060 = new Course("HonorsGeometry", "208060", 2);
        Course HonorsItalian3_508170 = new Course("HonorsItalian3", "508170", 1);
        Course HonorsItalian4_508180 = new Course("HonorsItalian4", "508180", 1);
        Course HonorsPreCalculus_208110 = new Course("HonorsPreCalculus", "208110", 2);
        Course HonorsScienceReseach_308110 = new Course("HonorsScienceReseach", "308110", 2, .5);
        Course HonorsShapersoftheWorld_108010 = new Course("HonorsShapersoftheWorld", "108010", 1);
        Course HonorsSpanish3_508160 = new Course("HonorsSpanish3", "508160", 2);
        Course HonorsSpanish4_508110 = new Course("HonorsSpanish4", "508110", 3);
        Course HonorsWindEnsemble_638100 = new Course("HonorsWindEnsemble", "638100", 1);
        Course IntroductionToPsychology_401070 = new Course("IntroductionToPsychology", "401070", 1);
        Course IntrotoHumanitiesResearch_101033 = new Course("IntrotoHumanitiesResearch", "101033", 6, 0.5);
        Course IntrotoHumanitiesResearch_101033i = new Course("IntrotoHumanitiesResearchI", "101033i", 1, 0.5);
        Course IntrotoHumanitiesResearch_101033s = new Course("IntrotoHumanitiesResearchS", "101033s", 1, 0.5);
        Course IntrotoScienceResearch_301033 = new Course("IntrotoScienceResearch", "301033", 1, 0.5);
        Course Italian2_501160 = new Course("Italian2", "501160", 2);
        Course Italian3_501170 = new Course("Italian3", "501170", 1);
        Course Italian4_501180 = new Course("Italian4", "501180", 1);
        Course LatinI_501200 = new Course("LatinI", "501200", 1);
        Course LearningCenter10_921002 = new Course("LearningCenter10", "921002", 2);
        Course LearningCenter10_921023 = new Course("LearningCenter10", "921023", 2, 0.5);
        Course LearningCenter11_921003 = new Course("LearningCenter11", "921003", 3);
        Course LearningCenter11_921033 = new Course("LearningCenter11", "921033", 3, 0.5);
        Course LearningCenter12_921004 = new Course("LearningCenter12", "921004", 3);
        Course LearningCenter12_921043 = new Course("LearningCenter12", "921043", 3, 0.5);
        Course LearningCenter9_921001 = new Course("LearningCenter9", "921001", 2);
        Course LearningCenter9_921013 = new Course("LearningCenter9", "921013", 2, 0.5);
        Course MusicProduction_631073 = new Course("MusicProduction", "631073", 2, 0.5);
        Course PersonalFitness_881053 = new Course("PersonalFitness", "881053", 2, 0.5);
        Course Photography_611070 = new Course("Photography", "611070", 2);
        Course PhotographyPortfolio_611100 = new Course("PhotographyPortfolio", "611100", 1);
        Course PhotographyPortfolio_611103 = new Course("PhotographyPortfolio", "611103", 1, 0.5);
        Course PhotoII_611083 = new Course("PhotoII", "611083", 2, 0.5);
        Course PhysicalEducation10_12_801023 = new Course("PhysicalEducation10_12", "801023", 8, 0.5);
        Course PhysicalEducation9_801013 = new Course("PhysicalEducation9", "801013", 6, 0.5);
        Course Physicswlab_PH301090 = new Course("Physicswlab", "PH301090", 3, 1.5);
        Course PortfolioDevelopment_611050 = new Course("PortfolioDevelopment", "611050", 1);
        Course PortfolioDevelopment2_611060 = new Course("PortfolioDevelopment2", "611060", 1);
        Course PreCalculus_201100 = new Course("PreCalculus", "201100", 3);
        Course Robotics_721063 = new Course("Robotics", "721063", 1, 0.5);
        Course ScienceandSociety_301120 = new Course("ScienceandSociety", "301120", 1);
        Course Spanish1_501070 = new Course("Spanish1", "501070", 1);
        Course Spanish2_501080 = new Course("Spanish2", "501080", 3);
        Course Spanish3_501090 = new Course("Spanish3", "501090", 1);
        Course Spanish4_501100 = new Course("Spanish4", "501100", 2);
        Course StudioinArt_611010 = new Course("StudioinArt", "611010", 4);
        Course StudioinArtII_611033 = new Course("StudioinArtII", "611033", 2, 0.5);
        Course SUPACollegeItalian_507020 = new Course("SUPACollegeItalian", "507020", 1);
        Course SUPACollegeSpanish_507010 = new Course("SUPACollegeSpanish", "507010", 2);
        Course TacticalSports_801063 = new Course("TacticalSports", "801063", 3, 0.5);
        Course TheaterArtsIIPerformanceWorkshop_620203 = new Course("TheaterArtsIIPerformanceWorkshop", "620203", 1, 0.5);
        Course TopicsinAlgebraII_201110 = new Course("TopicsinAlgebraII", "201110", 1);
        Course TopicsinAppliedMath_201160 = new Course("TopicsinAppliedMath", "201160", 1);
        Course UnitedStatesHistory_401030 = new Course("UnitedStatesHistory", "401030", 3);
        Course UnitedStatesHistory_401030I = new Course("UnitedStatesHistoryI", "401030I", 1);
        Course WindEnsemble_631103 = new Course("WindEnsemble", "631103", 1, 0.5);

        Accounting_711010.add(Stu1, Stu12, Stu14, Stu20, Stu30, Stu32, Stu50, Stu55, Stu56, Stu82, Stu98, Stu114, Stu120, Stu122, Stu150, Stu162, Stu166, Stu179, Stu207, Stu210, Stu216, Stu227, Stu260, Stu266, Stu296, Stu305, Stu306, Stu312, Stu321, Stu324, Stu328, Stu346, Stu378, Stu380, Stu396, Stu400, Stu415, Stu418, Stu424, Stu462);
        AdvancedArt_611040.add(Stu39, Stu86, Stu92, Stu95, Stu106, Stu183, Stu199, Stu207, Stu246, Stu252, Stu291, Stu352, Stu456, Stu457, Stu468);
        AdvancedPhotoPortfolio_611130.add(Stu310, Stu403, Stu429);
        AlgebraI_201020.add(Stu9, Stu13, Stu45, Stu48, Stu51, Stu57, Stu62, Stu74, Stu76, Stu77, Stu81, Stu97, Stu119, Stu124, Stu126, Stu132, Stu134, Stu141, Stu151, Stu154, Stu167, Stu192, Stu211, Stu217, Stu219, Stu223, Stu234, Stu255, Stu269, Stu271, Stu292, Stu316, Stu326, Stu333, Stu341, Stu348, Stu361, Stu381, Stu391, Stu399, Stu407, Stu411, Stu426, Stu427, Stu441, Stu443, Stu458);
        AlgebraIITrigonometry_201080.add(Stu5, Stu6, Stu40, Stu43, Stu55, Stu66, Stu85, Stu98, Stu102, Stu106, Stu120, Stu122, Stu129, Stu137, Stu155, Stu161, Stu172, Stu179, Stu188, Stu203, Stu216, Stu227, Stu237, Stu249, Stu251, Stu256, Stu274, Stu279, Stu295, Stu329, Stu335, Stu339, Stu351, Stu355, Stu368, Stu371, Stu383, Stu400, Stu409, Stu413, Stu424, Stu456, Stu457, Stu459, Stu468);
        AlgebraIYr1_201021.add(Stu232, Stu379);
        AlgebraIYr2_201210.add(Stu28, Stu298, Stu336);
        AlgebrawLab_201010.add(Stu11, Stu21, Stu36, Stu83, Stu100, Stu127, Stu214, Stu273);
        AlgebrawLab_201010I.add(Stu84, Stu307, Stu384);
        AP2DDesign_619110.add(Stu220, Stu265, Stu393);
        APBiologywlab_PH309020.add(Stu29, Stu31, Stu38, Stu73, Stu78, Stu86, Stu88, Stu95, Stu96, Stu99, Stu101, Stu110, Stu125, Stu130, Stu135, Stu142, Stu149, Stu160, Stu175, Stu178, Stu187, Stu229, Stu240, Stu242, Stu246, Stu253, Stu276, Stu280, Stu282, Stu283, Stu285, Stu286, Stu303, Stu317, Stu329, Stu344, Stu367, Stu370, Stu404, Stu419, Stu447, Stu454, Stu460, Stu463);
        APCalculusAB_209130.add(Stu47, Stu71, Stu96, Stu101, Stu138, Stu142, Stu143, Stu145, Stu147, Stu148, Stu157, Stu180, Stu184, Stu186, Stu263, Stu284, Stu301, Stu322, Stu330, Stu337, Stu338, Stu350, Stu405, Stu410, Stu416, Stu429, Stu430, Stu432, Stu447);
        APCalculusBC_209140.add(Stu17, Stu68, Stu165, Stu176, Stu206, Stu224, Stu259, Stu276, Stu278, Stu290, Stu375, Stu389, Stu419, Stu435, Stu448);
        APComputerScience_729010.add(Stu33, Stu59, Stu68, Stu85, Stu117, Stu148, Stu187, Stu206, Stu213, Stu253, Stu254, Stu263, Stu272, Stu283, Stu299, Stu322, Stu354, Stu419, Stu448, Stu452, Stu465);
        APDrawing_Painting_619060.add(Stu24, Stu103, Stu267, Stu356);
        APEnglish11LangandComp_109060.add(Stu2, Stu5, Stu16, Stu25, Stu29, Stu33, Stu38, Stu78, Stu79, Stu92, Stu106, Stu135, Stu149, Stu152, Stu160, Stu172, Stu175, Stu178, Stu193, Stu200, Stu201, Stu202, Stu209, Stu212, Stu213, Stu218, Stu221, Stu229, Stu230, Stu235, Stu246, Stu247, Stu249, Stu251, Stu254, Stu283, Stu285, Stu286, Stu289, Stu291, Stu303, Stu317, Stu325, Stu347, Stu354, Stu355, Stu362, Stu367, Stu370, Stu371, Stu403, Stu404, Stu409, Stu437, Stu452, Stu454, Stu456, Stu460, Stu463, Stu467);
        APEnglish12LitandComp_109050.add(Stu4, Stu17, Stu52, Stu64, Stu68, Stu71, Stu89, Stu95, Stu99, Stu115, Stu116, Stu138, Stu143, Stu145, Stu147, Stu186, Stu194, Stu206, Stu208, Stu220, Stu226, Stu241, Stu244, Stu259, Stu263, Stu267, Stu278, Stu280, Stu290, Stu315, Stu327, Stu331, Stu337, Stu338, Stu340, Stu350, Stu358, Stu359, Stu365, Stu387, Stu393, Stu416, Stu420, Stu435, Stu442, Stu446, Stu447, Stu448, Stu469);
        APGovt_Politics_409100.add(Stu6, Stu17, Stu22, Stu46, Stu117, Stu152, Stu155, Stu180, Stu194, Stu197, Stu224, Stu247, Stu249, Stu331, Stu337, Stu344, Stu363, Stu387, Stu416, Stu469);
        APItalianLanguage_Culture_509190.add(Stu4, Stu10, Stu22, Stu72, Stu87, Stu88, Stu89, Stu96, Stu184, Stu280, Stu284, Stu313, Stu315, Stu332, Stu429, Stu449);
        APMacroeconomics_409090.add(Stu72, Stu87, Stu101, Stu115, Stu145, Stu147, Stu165, Stu172, Stu176, Stu184, Stu224, Stu256, Stu263, Stu270, Stu272, Stu276, Stu278, Stu284, Stu290, Stu301, Stu314, Stu327, Stu330, Stu332, Stu347, Stu350, Stu359, Stu366, Stu370, Stu375, Stu387, Stu389, Stu394, Stu401, Stu417, Stu432, Stu442, Stu449);
        APPhysics1wlab_PH309030.add(Stu17, Stu25, Stu59, Stu68, Stu71, Stu117, Stu138, Stu142, Stu145, Stu147, Stu148, Stu165, Stu176, Stu180, Stu186, Stu190, Stu206, Stu254, Stu259, Stu272, Stu277, Stu278, Stu289, Stu290, Stu291, Stu322, Stu337, Stu354, Stu375, Stu432, Stu448, Stu465);
        AppliedChemistry_301040.add(Stu20, Stu28, Stu30, Stu108, Stu166, Stu298, Stu324, Stu336, Stu390, Stu422);
        APPsychology_309120.add(Stu4, Stu5, Stu6, Stu22, Stu27, Stu32, Stu52, Stu70, Stu72, Stu87, Stu113, Stu115, Stu116, Stu122, Stu133, Stu136, Stu138, Stu143, Stu153, Stu157, Stu171, Stu173, Stu184, Stu185, Stu194, Stu197, Stu202, Stu213, Stu220, Stu224, Stu237, Stu241, Stu244, Stu263, Stu267, Stu270, Stu279, Stu300, Stu301, Stu310, Stu313, Stu314, Stu327, Stu330, Stu331, Stu338, Stu340, Stu350, Stu358, Stu359, Stu362, Stu363, Stu365, Stu366, Stu373, Stu377, Stu386, Stu387, Stu389, Stu393, Stu403, Stu405, Stu410, Stu413, Stu416, Stu417, Stu420, Stu429, Stu430, Stu433, Stu435, Stu436, Stu442, Stu446, Stu449, Stu457, Stu469);
        APPsychology_409120.add(Stu394);
        APSpanishLanguage_Culture_509140.add(Stu17, Stu47, Stu68, Stu71, Stu95, Stu99, Stu101, Stu103, Stu113, Stu116, Stu143, Stu176, Stu186, Stu224, Stu241, Stu259, Stu276, Stu278, Stu331, Stu337, Stu338, Stu344, Stu350, Stu358, Stu363, Stu365, Stu366, Stu375, Stu393, Stu405, Stu410, Stu416, Stu417, Stu432, Stu435, Stu442, Stu447, Stu448, Stu469);
        APStatistics_209120.add(Stu25, Stu73, Stu96, Stu130, Stu142, Stu145, Stu148, Stu157, Stu176, Stu180, Stu184, Stu186, Stu190, Stu206, Stu240, Stu259, Stu276, Stu277, Stu280, Stu290, Stu338, Stu345, Stu375, Stu410, Stu435, Stu437, Stu447);
        APUSHistory_409040.add(Stu2, Stu16, Stu33, Stu38, Stu39, Stu40, Stu59, Stu85, Stu117, Stu125, Stu130, Stu133, Stu135, Stu149, Stu150, Stu178, Stu187, Stu188, Stu193, Stu200, Stu201, Stu209, Stu212, Stu218, Stu229, Stu230, Stu235, Stu243, Stu253, Stu277, Stu282, Stu285, Stu286, Stu291, Stu317, Stu325, Stu339, Stu345, Stu347, Stu353, Stu362, Stu367, Stu401, Stu404, Stu419, Stu438, Stu456, Stu463, Stu465, Stu467);
        BiologywithLAB_PH301010.add(Stu3, Stu7, Stu8, Stu9, Stu11, Stu13, Stu15, Stu18, Stu19, Stu21, Stu26, Stu36, Stu45, Stu48, Stu51, Stu57, Stu58, Stu62, Stu65, Stu67, Stu74, Stu76, Stu80, Stu81, Stu83, Stu84, Stu93, Stu97, Stu100, Stu104, Stu105, Stu107, Stu112, Stu119, Stu121, Stu123, Stu124, Stu126, Stu127, Stu128, Stu131, Stu132, Stu134, Stu139, Stu141, Stu144, Stu146, Stu151, Stu154, Stu156, Stu158, Stu159, Stu167, Stu169, Stu170, Stu182, Stu192, Stu195, Stu204, Stu211, Stu214, Stu217, Stu219, Stu222, Stu223, Stu225, Stu228, Stu234, Stu245, Stu255, Stu257, Stu262, Stu269, Stu271, Stu273, Stu275, Stu287, Stu288, Stu292, Stu297, Stu311, Stu316, Stu318, Stu323, Stu326, Stu333, Stu334, Stu341, Stu342, Stu348, Stu349, Stu357, Stu361, Stu376, Stu381, Stu382, Stu391, Stu395, Stu398, Stu399, Stu406, Stu407, Stu408, Stu411, Stu412, Stu426, Stu427, Stu434, Stu440, Stu441, Stu443, Stu444, Stu450, Stu451, Stu455, Stu458, Stu461, Stu464, Stu466, Stu471);
        BiologywithLab_PH30101I.add(Stu77, Stu232, Stu307, Stu379, Stu384);
        Calculus_201120.add(Stu22, Stu87, Stu99, Stu103, Stu110, Stu115, Stu208, Stu241, Stu242, Stu280, Stu327, Stu332, Stu344, Stu358, Stu365, Stu366, Stu387, Stu393, Stu417);
        Chemistrywlab_PH301050.add(Stu1, Stu12, Stu34, Stu41, Stu44, Stu50, Stu56, Stu60, Stu66, Stu92, Stu109, Stu111, Stu114, Stu161, Stu163, Stu168, Stu174, Stu179, Stu181, Stu189, Stu203, Stu221, Stu233, Stu248, Stu260, Stu266, Stu274, Stu279, Stu294, Stu295, Stu304, Stu312, Stu321, Stu328, Stu335, Stu346, Stu364, Stu368, Stu383, Stu388, Stu400, Stu415, Stu418, Stu462, Stu468, Stu470);
        Chorus_621010.add(Stu51, Stu54, Stu60, Stu75, Stu112, Stu132, Stu292, Stu357, Stu381, Stu451);
        ChorusABDay_621013.add(Stu33, Stu56, Stu260);
        CollegeAccounting_717020.add(Stu16, Stu22, Stu27, Stu29, Stu39, Stu40, Stu46, Stu73, Stu78, Stu79, Stu110, Stu129, Stu152, Stu155, Stu160, Stu172, Stu185, Stu201, Stu209, Stu212, Stu230, Stu256, Stu268, Stu322, Stu325, Stu339, Stu351, Stu355, Stu371, Stu372, Stu373, Stu374, Stu377, Stu401, Stu405, Stu420, Stu430, Stu433, Stu435, Stu452, Stu460, Stu467);
        CollegeChorus_627010.add(Stu94, Stu175, Stu186, Stu190, Stu226, Stu249, Stu365, Stu402);
        CollegeMarketing_717030.add(Stu4, Stu22, Stu24, Stu27, Stu35, Stu37, Stu42, Stu52, Stu64, Stu70, Stu71, Stu87, Stu88, Stu89, Stu94, Stu101, Stu110, Stu113, Stu116, Stu118, Stu136, Stu153, Stu171, Stu173, Stu180, Stu183, Stu197, Stu208, Stu220, Stu244, Stu264, Stu267, Stu270, Stu278, Stu284, Stu300, Stu301, Stu308, Stu310, Stu313, Stu314, Stu315, Stu331, Stu332, Stu340, Stu358, Stu359, Stu363, Stu366, Stu369, Stu372, Stu373, Stu386, Stu389, Stu393, Stu417, Stu420, Stu429, Stu446, Stu449);
        CollegeWindEnsemble_637010.add(Stu193, Stu240, Stu290, Stu337, Stu353, Stu367, Stu448, Stu463);
        ComparativeLiterature_101050.add(Stu10, Stu22, Stu42, Stu88, Stu94, Stu96, Stu110, Stu118, Stu136, Stu142, Stu148, Stu153, Stu157, Stu165, Stu171, Stu180, Stu183, Stu197, Stu224, Stu242, Stu264, Stu265, Stu270, Stu272, Stu276, Stu284, Stu300, Stu301, Stu302, Stu310, Stu322, Stu332, Stu363, Stu369, Stu377, Stu386, Stu389, Stu402, Stu405, Stu410, Stu417, Stu429, Stu430, Stu432, Stu433);
        ComparativeLiterature_101050I.add(Stu82, Stu173, Stu296, Stu314, Stu320, Stu356);
        ComparativeLiterature2_101050i2.add(Stu14, Stu24, Stu37, Stu70, Stu308, Stu372);
        ConcertBand_631010.add(Stu18, Stu58, Stu65, Stu121, Stu134, Stu154, Stu156, Stu222, Stu287, Stu288, Stu318, Stu342, Stu376, Stu384, Stu408, Stu426, Stu427, Stu441, Stu461, Stu466, Stu471);
        ConcertBand_631013.add(Stu1, Stu114, Stu233, Stu346, Stu383, Stu421);
        ConstitutionalLaw_401083.add(Stu4, Stu42, Stu47, Stu52, Stu68, Stu71, Stu72, Stu87, Stu88, Stu99, Stu103, Stu113, Stu136, Stu142, Stu143, Stu145, Stu157, Stu184, Stu208, Stu220, Stu241, Stu244, Stu259, Stu264, Stu265, Stu276, Stu300, Stu301, Stu302, Stu310, Stu314, Stu330, Stu338, Stu340, Stu350, Stu356, Stu365, Stu366, Stu372, Stu373, Stu375, Stu377, Stu389, Stu410, Stu420, Stu429, Stu432, Stu433, Stu448, Stu449);
        ConstitutionalLaw_401083I.add(Stu14, Stu24, Stu35, Stu37, Stu82, Stu173, Stu296, Stu308);
        ContemporaryIssues_401093.add(Stu10, Stu38, Stu70, Stu89, Stu94, Stu95, Stu96, Stu101, Stu106, Stu116, Stu118, Stu129, Stu138, Stu148, Stu153, Stu183, Stu206, Stu226, Stu242, Stu251, Stu263, Stu270, Stu272, Stu278, Stu280, Stu284, Stu305, Stu313, Stu315, Stu317, Stu320, Stu322, Stu327, Stu332, Stu339, Stu344, Stu369, Stu402, Stu405, Stu413, Stu430);
        DigitalPhotography_611093.add(Stu12, Stu13, Stu18, Stu36, Stu64, Stu81, Stu105, Stu115, Stu125, Stu147, Stu181, Stu187, Stu245, Stu250, Stu262, Stu269, Stu292, Stu309, Stu315, Stu317, Stu319, Stu362, Stu379, Stu381, Stu392, Stu398, Stu400, Stu427, Stu441, Stu450, Stu458);
        EarthSciencewlab_PH301150.add(Stu53, Stu61, Stu90, Stu196, Stu231, Stu261, Stu281, Stu293, Stu296, Stu306, Stu309, Stu360, Stu378, Stu385, Stu392, Stu428, Stu431, Stu453);
        Economics_401053.add(Stu4, Stu10, Stu17, Stu27, Stu42, Stu47, Stu52, Stu64, Stu68, Stu70, Stu71, Stu88, Stu89, Stu94, Stu95, Stu96, Stu99, Stu103, Stu110, Stu113, Stu116, Stu118, Stu136, Stu138, Stu142, Stu143, Stu153, Stu157, Stu171, Stu183, Stu186, Stu194, Stu208, Stu220, Stu226, Stu241, Stu242, Stu244, Stu259, Stu264, Stu265, Stu267, Stu280, Stu300, Stu302, Stu310, Stu313, Stu315, Stu320, Stu322, Stu331, Stu337, Stu338, Stu340, Stu358, Stu363, Stu365, Stu369, Stu372, Stu373, Stu377, Stu386, Stu393, Stu402, Stu405, Stu410, Stu429, Stu430, Stu433, Stu435, Stu446, Stu447, Stu469);
        Economics_401053I.add(Stu14, Stu24, Stu35, Stu37, Stu82, Stu173, Stu296, Stu308, Stu356);
        English10_101030.add(Stu1, Stu12, Stu23, Stu30, Stu34, Stu41, Stu43, Stu44, Stu50, Stu53, Stu54, Stu56, Stu60, Stu61, Stu63, Stu66, Stu69, Stu90, Stu102, Stu108, Stu109, Stu111, Stu114, Stu140, Stu161, Stu162, Stu163, Stu164, Stu168, Stu174, Stu177, Stu179, Stu181, Stu189, Stu191, Stu196, Stu198, Stu203, Stu205, Stu210, Stu215, Stu231, Stu233, Stu236, Stu238, Stu239, Stu248, Stu258, Stu260, Stu261, Stu266, Stu274, Stu281, Stu293, Stu294, Stu295, Stu304, Stu306, Stu309, Stu312, Stu319, Stu321, Stu324, Stu328, Stu335, Stu336, Stu343, Stu346, Stu360, Stu364, Stu368, Stu378, Stu380, Stu383, Stu385, Stu388, Stu392, Stu396, Stu397, Stu400, Stu414, Stu415, Stu418, Stu421, Stu425, Stu428, Stu453, Stu459, Stu462, Stu468, Stu470);
        English10_101030I.add(Stu20, Stu28, Stu166, Stu298, Stu422, Stu431, Stu445);
        English10_101030S.add(Stu390);
        English11_101040.add(Stu6, Stu31, Stu32, Stu39, Stu40, Stu46, Stu49, Stu55, Stu59, Stu73, Stu85, Stu86, Stu98, Stu117, Stu122, Stu125, Stu129, Stu130, Stu133, Stu150, Stu155, Stu185, Stu187, Stu188, Stu190, Stu207, Stu216, Stu237, Stu240, Stu243, Stu252, Stu253, Stu256, Stu268, Stu277, Stu279, Stu282, Stu299, Stu305, Stu329, Stu339, Stu345, Stu351, Stu353, Stu374, Stu401, Stu413, Stu419, Stu423, Stu424, Stu436, Stu438, Stu439, Stu457, Stu465);
        English11_101040I.add(Stu75, Stu91, Stu120, Stu137, Stu199, Stu227, Stu250, Stu352, Stu394);
        English9_101010.add(Stu3, Stu7, Stu8, Stu9, Stu11, Stu13, Stu15, Stu18, Stu19, Stu21, Stu26, Stu36, Stu45, Stu48, Stu51, Stu57, Stu58, Stu62, Stu65, Stu67, Stu80, Stu81, Stu83, Stu93, Stu97, Stu100, Stu104, Stu105, Stu107, Stu112, Stu119, Stu121, Stu123, Stu124, Stu126, Stu127, Stu128, Stu131, Stu132, Stu134, Stu139, Stu141, Stu144, Stu146, Stu151, Stu154, Stu156, Stu158, Stu159, Stu167, Stu169, Stu170, Stu182, Stu192, Stu195, Stu204, Stu211, Stu214, Stu217, Stu219, Stu222, Stu223, Stu225, Stu228, Stu234, Stu245, Stu255, Stu257, Stu262, Stu269, Stu271, Stu273, Stu275, Stu287, Stu288, Stu292, Stu297, Stu311, Stu316, Stu318, Stu323, Stu326, Stu333, Stu334, Stu341, Stu342, Stu348, Stu349, Stu357, Stu361, Stu376, Stu381, Stu382, Stu391, Stu395, Stu398, Stu399, Stu406, Stu407, Stu408, Stu411, Stu412, Stu426, Stu427, Stu434, Stu440, Stu441, Stu443, Stu444, Stu450, Stu451, Stu455, Stu458, Stu461, Stu464, Stu466, Stu471);
        English9_101010I.add(Stu74, Stu76, Stu84, Stu307, Stu379, Stu384);
        English9_101010S.add(Stu77, Stu232);
        ESL_101011.add(Stu381);
        ForensicScience_301130.add(Stu5, Stu32, Stu43, Stu49, Stu55, Stu64, Stu89, Stu122, Stu129, Stu153, Stu185, Stu188, Stu194, Stu208, Stu216, Stu227, Stu237, Stu249, Stu251, Stu252, Stu268, Stu303, Stu305, Stu369, Stu394, Stu413, Stu424, Stu436, Stu438, Stu457);
        ForensicScience_301130i.add(Stu24, Stu37, Stu75, Stu91, Stu150, Stu199, Stu250, Stu352, Stu409, Stu439);
        Geometry_201050.add(Stu3, Stu7, Stu12, Stu30, Stu34, Stu43, Stu44, Stu53, Stu66, Stu90, Stu108, Stu114, Stu123, Stu139, Stu158, Stu163, Stu169, Stu170, Stu174, Stu189, Stu191, Stu196, Stu204, Stu231, Stu281, Stu293, Stu306, Stu309, Stu311, Stu321, Stu349, Stu378, Stu385, Stu392, Stu397, Stu418, Stu428, Stu431, Stu453, Stu455);
        GeometryB_201040.add(Stu20, Stu61, Stu75, Stu166, Stu250, Stu324, Stu390, Stu422);
        GlobalHistory_Geography10_401020.add(Stu1, Stu12, Stu23, Stu30, Stu34, Stu41, Stu43, Stu44, Stu50, Stu53, Stu54, Stu56, Stu60, Stu61, Stu63, Stu66, Stu69, Stu90, Stu102, Stu108, Stu109, Stu111, Stu114, Stu140, Stu161, Stu162, Stu163, Stu164, Stu168, Stu174, Stu177, Stu179, Stu181, Stu189, Stu191, Stu196, Stu198, Stu203, Stu205, Stu210, Stu215, Stu231, Stu233, Stu236, Stu238, Stu239, Stu248, Stu258, Stu260, Stu261, Stu266, Stu274, Stu281, Stu293, Stu294, Stu295, Stu304, Stu306, Stu309, Stu312, Stu319, Stu321, Stu324, Stu328, Stu335, Stu336, Stu343, Stu346, Stu360, Stu364, Stu368, Stu378, Stu380, Stu383, Stu385, Stu388, Stu392, Stu396, Stu397, Stu400, Stu414, Stu415, Stu418, Stu421, Stu425, Stu428, Stu453, Stu459, Stu462, Stu468, Stu470);
        GlobalHistory_Geography10_401020I.add(Stu20, Stu28, Stu166, Stu298, Stu390, Stu422, Stu431, Stu445);
        GlobalHistory_Geography9_401010.add(Stu3, Stu7, Stu8, Stu9, Stu11, Stu13, Stu15, Stu18, Stu19, Stu21, Stu26, Stu36, Stu45, Stu48, Stu51, Stu57, Stu58, Stu62, Stu65, Stu67, Stu74, Stu76, Stu80, Stu81, Stu83, Stu84, Stu93, Stu97, Stu100, Stu104, Stu105, Stu107, Stu112, Stu119, Stu121, Stu123, Stu124, Stu126, Stu127, Stu128, Stu131, Stu132, Stu134, Stu139, Stu141, Stu144, Stu146, Stu151, Stu154, Stu156, Stu158, Stu159, Stu167, Stu169, Stu170, Stu182, Stu192, Stu195, Stu204, Stu211, Stu214, Stu217, Stu219, Stu222, Stu223, Stu225, Stu228, Stu234, Stu245, Stu255, Stu257, Stu262, Stu269, Stu271, Stu273, Stu275, Stu287, Stu288, Stu292, Stu297, Stu311, Stu316, Stu318, Stu323, Stu326, Stu333, Stu334, Stu341, Stu342, Stu348, Stu349, Stu357, Stu361, Stu376, Stu381, Stu382, Stu391, Stu395, Stu398, Stu399, Stu406, Stu407, Stu408, Stu411, Stu412, Stu426, Stu427, Stu434, Stu440, Stu441, Stu443, Stu444, Stu450, Stu451, Stu455, Stu458, Stu461, Stu464, Stu466, Stu471);
        GlobalHistory_Geography9_401010I.add(Stu77, Stu232, Stu307, Stu379, Stu384);
        Health_881063.add(Stu1, Stu12, Stu20, Stu23, Stu28, Stu30, Stu34, Stu41, Stu43, Stu44, Stu50, Stu53, Stu54, Stu56, Stu60, Stu61, Stu63, Stu66, Stu69, Stu90, Stu102, Stu108, Stu109, Stu111, Stu114, Stu140, Stu161, Stu162, Stu163, Stu164, Stu166, Stu168, Stu174, Stu177, Stu179, Stu181, Stu189, Stu191, Stu196, Stu198, Stu203, Stu205, Stu210, Stu215, Stu231, Stu233, Stu236, Stu238, Stu239, Stu248, Stu258, Stu260, Stu261, Stu266, Stu274, Stu281, Stu293, Stu294, Stu295, Stu298, Stu304, Stu306, Stu309, Stu312, Stu319, Stu321, Stu324, Stu328, Stu335, Stu336, Stu343, Stu346, Stu360, Stu364, Stu368, Stu378, Stu380, Stu383, Stu385, Stu388, Stu390, Stu392, Stu396, Stu397, Stu400, Stu414, Stu415, Stu418, Stu421, Stu422, Stu425, Stu428, Stu431, Stu445, Stu453, Stu459, Stu462, Stu468, Stu470);
        HonorsAlgebraIITrigonometry_208090.add(Stu1, Stu23, Stu50, Stu54, Stu56, Stu60, Stu63, Stu69, Stu109, Stu111, Stu112, Stu140, Stu149, Stu162, Stu164, Stu168, Stu177, Stu181, Stu198, Stu205, Stu209, Stu210, Stu215, Stu230, Stu233, Stu236, Stu238, Stu239, Stu243, Stu248, Stu253, Stu258, Stu260, Stu261, Stu266, Stu268, Stu289, Stu294, Stu304, Stu312, Stu319, Stu328, Stu343, Stu346, Stu364, Stu380, Stu388, Stu394, Stu396, Stu401, Stu404, Stu414, Stu415, Stu421, Stu425, Stu436, Stu445, Stu462, Stu470);
        HonorsChemistrywlab_PH308060.add(Stu23, Stu54, Stu63, Stu69, Stu102, Stu140, Stu162, Stu164, Stu177, Stu191, Stu198, Stu205, Stu210, Stu215, Stu236, Stu238, Stu239, Stu258, Stu319, Stu343, Stu380, Stu396, Stu397, Stu414, Stu421, Stu425, Stu445, Stu459);
        HonorsComputerScience_728010.add(Stu2, Stu31, Stu34, Stu82, Stu96, Stu108, Stu115, Stu125, Stu135, Stu138, Stu161, Stu164, Stu168, Stu175, Stu177, Stu181, Stu197, Stu198, Stu215, Stu218, Stu224, Stu229, Stu238, Stu239, Stu258, Stu270, Stu277, Stu289, Stu294, Stu295, Stu301, Stu304, Stu319, Stu325, Stu330, Stu343, Stu345, Stu353, Stu364, Stu380, Stu383, Stu421, Stu423, Stu459);
        HonorsEngineeringDesign_728020.add(Stu41, Stu63, Stu109, Stu111, Stu140, Stu163, Stu189, Stu205, Stu233, Stu236, Stu253, Stu254, Stu274, Stu335, Stu425);
        HonorsGeometry_208060.add(Stu8, Stu15, Stu18, Stu19, Stu26, Stu41, Stu58, Stu65, Stu67, Stu80, Stu93, Stu104, Stu105, Stu107, Stu121, Stu128, Stu131, Stu144, Stu146, Stu156, Stu159, Stu182, Stu195, Stu222, Stu225, Stu228, Stu245, Stu257, Stu262, Stu275, Stu287, Stu288, Stu297, Stu318, Stu323, Stu334, Stu342, Stu357, Stu360, Stu376, Stu382, Stu395, Stu398, Stu406, Stu408, Stu412, Stu434, Stu440, Stu444, Stu450, Stu451, Stu461, Stu464, Stu466, Stu471);
        HonorsItalian3_508170.add(Stu39, Stu56, Stu60, Stu90, Stu102, Stu109, Stu111, Stu163, Stu236, Stu248, Stu260, Stu261, Stu281, Stu294, Stu295, Stu306, Stu346, Stu383, Stu388);
        HonorsItalian4_508180.add(Stu85, Stu86, Stu117, Stu178, Stu185, Stu202, Stu218, Stu251, Stu268, Stu285, Stu329, Stu354, Stu460, Stu465);
        HonorsPreCalculus_208110.add(Stu2, Stu25, Stu33, Stu59, Stu73, Stu117, Stu125, Stu130, Stu133, Stu135, Stu152, Stu160, Stu175, Stu178, Stu187, Stu190, Stu197, Stu200, Stu213, Stu229, Stu240, Stu246, Stu247, Stu254, Stu277, Stu282, Stu283, Stu291, Stu299, Stu345, Stu347, Stu354, Stu367, Stu370, Stu437, Stu452, Stu454, Stu460, Stu463, Stu465, Stu467);
        HonorsScienceReseach_308110.add(Stu17, Stu23, Stu38, Stu47, Stu68, Stu69, Stu99, Stu110, Stu187, Stu206, Stu259, Stu280, Stu282, Stu283, Stu289, Stu290, Stu337, Stu350, Stu354, Stu432);
        HonorsShapersoftheWorld_108010.add(Stu27, Stu35, Stu47, Stu72, Stu87, Stu101, Stu103, Stu113, Stu176, Stu184, Stu300, Stu313, Stu330, Stu344, Stu366, Stu373, Stu375, Stu449);
        HonorsSpanish3_508160.add(Stu1, Stu12, Stu23, Stu34, Stu41, Stu44, Stu50, Stu54, Stu63, Stu69, Stu140, Stu161, Stu162, Stu168, Stu177, Stu179, Stu198, Stu203, Stu205, Stu210, Stu215, Stu238, Stu239, Stu258, Stu266, Stu312, Stu319, Stu321, Stu328, Stu343, Stu360, Stu364, Stu385, Stu396, Stu397, Stu414, Stu415, Stu418, Stu421, Stu425, Stu459, Stu462, Stu468, Stu470);
        HonorsSpanish4_508110.add(Stu5, Stu6, Stu29, Stu31, Stu32, Stu33, Stu38, Stu40, Stu59, Stu73, Stu78, Stu79, Stu125, Stu129, Stu130, Stu133, Stu135, Stu149, Stu152, Stu155, Stu160, Stu175, Stu187, Stu190, Stu193, Stu200, Stu201, Stu212, Stu213, Stu216, Stu229, Stu230, Stu237, Stu240, Stu246, Stu247, Stu249, Stu277, Stu279, Stu282, Stu283, Stu286, Stu289, Stu291, Stu299, Stu303, Stu325, Stu339, Stu345, Stu347, Stu362, Stu367, Stu370, Stu371, Stu404, Stu413, Stu419, Stu437, Stu452, Stu454, Stu457, Stu463, Stu467);
        HonorsWindEnsemble_638100.add(Stu23, Stu30, Stu368, Stu397);
        IntroductionToPsychology_401070.add(Stu16, Stu28, Stu44, Stu46, Stu75, Stu92, Stu178, Stu191, Stu196, Stu203, Stu243, Stu248, Stu281, Stu286, Stu309, Stu329, Stu336, Stu352, Stu369, Stu385, Stu404, Stu409, Stu414, Stu439, Stu453, Stu470);
        IntrotoHumanitiesResearch_101033.add(Stu3, Stu7, Stu8, Stu9, Stu11, Stu13, Stu15, Stu18, Stu19, Stu21, Stu26, Stu36, Stu45, Stu48, Stu51, Stu57, Stu58, Stu62, Stu65, Stu67, Stu80, Stu81, Stu83, Stu93, Stu97, Stu100, Stu104, Stu105, Stu107, Stu112, Stu119, Stu121, Stu123, Stu124, Stu126, Stu127, Stu128, Stu131, Stu132, Stu134, Stu139, Stu141, Stu144, Stu146, Stu151, Stu154, Stu156, Stu158, Stu159, Stu167, Stu169, Stu170, Stu182, Stu192, Stu195, Stu204, Stu211, Stu214, Stu217, Stu219, Stu222, Stu223, Stu225, Stu228, Stu234, Stu245, Stu255, Stu257, Stu262, Stu269, Stu271, Stu273, Stu275, Stu287, Stu288, Stu292, Stu297, Stu311, Stu316, Stu318, Stu323, Stu326, Stu333, Stu334, Stu341, Stu342, Stu348, Stu349, Stu357, Stu361, Stu376, Stu381, Stu382, Stu391, Stu395, Stu398, Stu399, Stu406, Stu407, Stu408, Stu411, Stu412, Stu426, Stu427, Stu434, Stu440, Stu441, Stu443, Stu444, Stu450, Stu451, Stu455, Stu458, Stu461, Stu464, Stu466, Stu471);
        IntrotoHumanitiesResearch_101033i.add(Stu74, Stu76, Stu84, Stu307, Stu379, Stu384);
        IntrotoHumanitiesResearch_101033s.add(Stu77, Stu232);
        IntrotoScienceResearch_301033.add(Stu7, Stu8, Stu19, Stu124, Stu167, Stu195, Stu222, Stu225, Stu257, Stu323, Stu376, Stu412, Stu426);
        Italian2_501160.add(Stu3, Stu7, Stu8, Stu9, Stu11, Stu13, Stu36, Stu45, Stu48, Stu57, Stu58, Stu62, Stu74, Stu76, Stu77, Stu81, Stu83, Stu84, Stu93, Stu97, Stu100, Stu119, Stu123, Stu124, Stu127, Stu128, Stu134, Stu141, Stu146, Stu151, Stu156, Stu158, Stu167, Stu234, Stu262, Stu271, Stu273, Stu275, Stu292, Stu311, Stu333, Stu334, Stu341, Stu348, Stu379, Stu398, Stu411, Stu450, Stu451, Stu464, Stu466, Stu471);
        Italian3_501170.add(Stu53, Stu61, Stu164, Stu231, Stu233, Stu309, Stu336, Stu453);
        Italian4_501180.add(Stu49, Stu137, Stu252, Stu374, Stu424);
        LatinI_501200.add(Stu118, Stu245, Stu262, Stu305, Stu390, Stu395, Stu422);
        LearningCenter10_921002.add(Stu20, Stu28, Stu166, Stu298, Stu336, Stu390, Stu422, Stu431);
        LearningCenter10_921023.add(Stu324);
        LearningCenter11_921003.add(Stu49, Stu75, Stu91, Stu120, Stu137, Stu150, Stu199, Stu227, Stu250, Stu352);
        LearningCenter11_921033.add(Stu409, Stu439);
        LearningCenter12_921004.add(Stu14, Stu24, Stu35, Stu37, Stu70, Stu82, Stu173, Stu296, Stu308, Stu314, Stu356);
        LearningCenter12_921043.add(Stu320, Stu372);
        LearningCenter9_921001.add(Stu74, Stu76, Stu77, Stu84, Stu232, Stu307, Stu379, Stu384);
        LearningCenter9_921013.add(Stu21);
        MusicProduction_631073.add(Stu21, Stu25, Stu74, Stu76, Stu77, Stu83, Stu109, Stu128, Stu164, Stu211, Stu235, Stu259, Stu293, Stu335, Stu341, Stu391, Stu431, Stu444, Stu445, Stu471);
        PersonalFitness_881053.add(Stu24, Stu31, Stu44, Stu50, Stu55, Stu56, Stu69, Stu72, Stu86, Stu87, Stu88, Stu113, Stu129, Stu135, Stu138, Stu148, Stu149, Stu153, Stu163, Stu177, Stu189, Stu190, Stu191, Stu207, Stu229, Stu241, Stu254, Stu260, Stu268, Stu299, Stu304, Stu367, Stu397, Stu414, Stu422, Stu433, Stu438, Stu439);
        Photography_611070.add(Stu8, Stu9, Stu15, Stu19, Stu26, Stu45, Stu57, Stu67, Stu97, Stu102, Stu104, Stu108, Stu126, Stu144, Stu149, Stu159, Stu182, Stu217, Stu223, Stu225, Stu226, Stu228, Stu275, Stu311, Stu323, Stu390, Stu434, Stu440, Stu443);
        PhotographyPortfolio_611100.add(Stu29, Stu221, Stu298);
        PhotographyPortfolio_611103.add(Stu66, Stu254, Stu396, Stu459);
        PhotoII_611083.add(Stu34, Stu41, Stu44, Stu50, Stu79, Stu162, Stu165, Stu189, Stu210, Stu231, Stu238, Stu248, Stu266, Stu281, Stu293, Stu304, Stu306, Stu312, Stu321, Stu378, Stu422, Stu425, Stu428);
        PhysicalEducation10_12_801023.add(Stu4, Stu5, Stu6, Stu10, Stu12, Stu14, Stu17, Stu23, Stu25, Stu27, Stu28, Stu29, Stu32, Stu33, Stu34, Stu35, Stu37, Stu38, Stu39, Stu40, Stu41, Stu42, Stu43, Stu47, Stu49, Stu52, Stu54, Stu59, Stu60, Stu63, Stu64, Stu66, Stu68, Stu70, Stu71, Stu73, Stu75, Stu78, Stu82, Stu85, Stu89, Stu91, Stu92, Stu94, Stu95, Stu96, Stu98, Stu99, Stu101, Stu102, Stu103, Stu106, Stu108, Stu109, Stu110, Stu111, Stu115, Stu116, Stu117, Stu122, Stu130, Stu136, Stu140, Stu143, Stu145, Stu147, Stu152, Stu155, Stu157, Stu160, Stu161, Stu164, Stu165, Stu168, Stu171, Stu173, Stu175, Stu176, Stu178, Stu179, Stu180, Stu183, Stu184, Stu185, Stu186, Stu187, Stu188, Stu193, Stu194, Stu196, Stu197, Stu200, Stu201, Stu202, Stu203, Stu205, Stu206, Stu208, Stu213, Stu215, Stu216, Stu218, Stu220, Stu221, Stu224, Stu226, Stu227, Stu230, Stu231, Stu233, Stu236, Stu237, Stu238, Stu239, Stu246, Stu247, Stu248, Stu249, Stu250, Stu251, Stu252, Stu253, Stu256, Stu258, Stu259, Stu263, Stu264, Stu265, Stu267, Stu270, Stu272, Stu274, Stu276, Stu278, Stu279, Stu280, Stu281, Stu282, Stu283, Stu284, Stu285, Stu286, Stu290, Stu291, Stu293, Stu298, Stu300, Stu301, Stu302, Stu303, Stu306, Stu308, Stu309, Stu310, Stu313, Stu314, Stu315, Stu317, Stu320, Stu321, Stu322, Stu328, Stu329, Stu330, Stu332, Stu335, Stu336, Stu337, Stu340, Stu343, Stu344, Stu347, Stu350, Stu351, Stu352, Stu354, Stu355, Stu356, Stu358, Stu360, Stu362, Stu364, Stu365, Stu366, Stu370, Stu371, Stu372, Stu373, Stu375, Stu377, Stu380, Stu385, Stu386, Stu387, Stu392, Stu393, Stu396, Stu400, Stu402, Stu403, Stu404, Stu405, Stu409, Stu410, Stu413, Stu415, Stu416, Stu417, Stu418, Stu424, Stu425, Stu428, Stu429, Stu430, Stu431, Stu432, Stu435, Stu436, Stu437, Stu442, Stu445, Stu446, Stu447, Stu448, Stu449, Stu452, Stu453, Stu454, Stu456, Stu457, Stu459, Stu460, Stu462, Stu463, Stu465, Stu468, Stu469, Stu470);
        PhysicalEducation9_801013.add(Stu3, Stu7, Stu8, Stu9, Stu11, Stu13, Stu15, Stu18, Stu19, Stu21, Stu26, Stu36, Stu45, Stu48, Stu51, Stu57, Stu58, Stu62, Stu65, Stu67, Stu74, Stu76, Stu77, Stu80, Stu81, Stu83, Stu84, Stu93, Stu97, Stu100, Stu104, Stu105, Stu107, Stu112, Stu119, Stu121, Stu123, Stu124, Stu126, Stu127, Stu128, Stu131, Stu132, Stu134, Stu139, Stu141, Stu144, Stu146, Stu151, Stu154, Stu156, Stu158, Stu159, Stu167, Stu169, Stu170, Stu182, Stu192, Stu195, Stu204, Stu211, Stu214, Stu217, Stu219, Stu222, Stu223, Stu225, Stu228, Stu232, Stu234, Stu245, Stu255, Stu257, Stu262, Stu269, Stu271, Stu273, Stu275, Stu287, Stu288, Stu292, Stu297, Stu307, Stu311, Stu316, Stu318, Stu323, Stu326, Stu333, Stu334, Stu341, Stu342, Stu348, Stu349, Stu357, Stu361, Stu376, Stu379, Stu381, Stu382, Stu384, Stu391, Stu395, Stu398, Stu399, Stu406, Stu407, Stu408, Stu411, Stu412, Stu426, Stu427, Stu434, Stu440, Stu441, Stu443, Stu444, Stu450, Stu451, Stu455, Stu458, Stu461, Stu464, Stu466, Stu471);
        Physicswlab_PH301090.add(Stu2, Stu6, Stu10, Stu16, Stu33, Stu39, Stu40, Stu46, Stu47, Stu72, Stu79, Stu85, Stu98, Stu99, Stu103, Stu106, Stu120, Stu133, Stu137, Stu143, Stu152, Stu155, Stu172, Stu188, Stu193, Stu194, Stu200, Stu201, Stu202, Stu207, Stu209, Stu212, Stu213, Stu218, Stu230, Stu235, Stu243, Stu247, Stu256, Stu268, Stu284, Stu299, Stu320, Stu325, Stu327, Stu332, Stu339, Stu345, Stu347, Stu351, Stu353, Stu355, Stu362, Stu371, Stu374, Stu387, Stu401, Stu403, Stu416, Stu423, Stu437, Stu442, Stu452, Stu456, Stu467, Stu469);
        PortfolioDevelopment_611050.add(Stu14, Stu230, Stu243, Stu302, Stu347, Stu374);
        PortfolioDevelopment2_611060.add(Stu241);
        PreCalculus_201100.add(Stu4, Stu10, Stu16, Stu27, Stu29, Stu31, Stu35, Stu38, Stu39, Stu46, Stu52, Stu64, Stu70, Stu72, Stu78, Stu79, Stu86, Stu88, Stu89, Stu95, Stu113, Stu116, Stu136, Stu153, Stu171, Stu183, Stu185, Stu193, Stu194, Stu201, Stu202, Stu212, Stu218, Stu220, Stu235, Stu244, Stu252, Stu265, Stu267, Stu270, Stu285, Stu286, Stu300, Stu303, Stu310, Stu313, Stu314, Stu315, Stu317, Stu320, Stu325, Stu331, Stu340, Stu353, Stu359, Stu362, Stu363, Stu369, Stu373, Stu374, Stu377, Stu386, Stu402, Stu403, Stu420, Stu423, Stu433, Stu442, Stu446, Stu449, Stu469);
        Robotics_721063.add(Stu15, Stu51, Stu58, Stu80, Stu83, Stu104, Stu108, Stu112, Stu126, Stu128, Stu131, Stu154, Stu182, Stu211, Stu232, Stu287, Stu299, Stu318, Stu349, Stu408, Stu451, Stu466);
        ScienceandSociety_301120.add(Stu35, Stu42, Stu94, Stu103, Stu183, Stu226, Stu242, Stu244, Stu264, Stu265, Stu302, Stu308, Stu315, Stu356, Stu372, Stu386, Stu402);
        Spanish1_501070.add(Stu67, Stu211, Stu214);
        Spanish2_501080.add(Stu15, Stu18, Stu19, Stu21, Stu25, Stu26, Stu43, Stu51, Stu65, Stu66, Stu80, Stu104, Stu105, Stu107, Stu112, Stu121, Stu126, Stu131, Stu132, Stu139, Stu144, Stu154, Stu159, Stu169, Stu170, Stu182, Stu191, Stu192, Stu195, Stu196, Stu204, Stu207, Stu217, Stu219, Stu222, Stu223, Stu225, Stu228, Stu245, Stu255, Stu257, Stu269, Stu287, Stu288, Stu297, Stu307, Stu316, Stu318, Stu323, Stu326, Stu342, Stu349, Stu357, Stu361, Stu376, Stu382, Stu391, Stu399, Stu406, Stu407, Stu408, Stu412, Stu426, Stu427, Stu431, Stu434, Stu440, Stu441, Stu443, Stu444, Stu455, Stu458, Stu461);
        Spanish3_501090.add(Stu30, Stu55, Stu114, Stu174, Stu181, Stu189, Stu242, Stu243, Stu274, Stu293, Stu335, Stu368, Stu378, Stu392, Stu400, Stu428);
        Spanish4_501100.add(Stu2, Stu16, Stu91, Stu98, Stu106, Stu122, Stu153, Stu172, Stu188, Stu209, Stu221, Stu235, Stu256, Stu300, Stu317, Stu351, Stu353, Stu355, Stu401, Stu403, Stu409, Stu423, Stu433, Stu436, Stu438, Stu439, Stu456);
        StudioinArt_611010.add(Stu3, Stu7, Stu11, Stu20, Stu48, Stu53, Stu61, Stu62, Stu80, Stu81, Stu90, Stu93, Stu100, Stu105, Stu107, Stu119, Stu123, Stu124, Stu127, Stu131, Stu133, Stu139, Stu141, Stu146, Stu150, Stu151, Stu158, Stu166, Stu167, Stu169, Stu170, Stu174, Stu192, Stu195, Stu204, Stu214, Stu219, Stu227, Stu232, Stu234, Stu235, Stu255, Stu257, Stu261, Stu269, Stu271, Stu273, Stu297, Stu316, Stu326, Stu333, Stu334, Stu341, Stu348, Stu349, Stu360, Stu361, Stu382, Stu388, Stu391, Stu395, Stu398, Stu399, Stu406, Stu407, Stu411, Stu412, Stu444, Stu450, Stu455, Stu458, Stu464);
        StudioinArtII_611033.add(Stu28, Stu43, Stu55, Stu69, Stu88, Stu113, Stu120, Stu137, Stu179, Stu191, Stu203, Stu231, Stu264, Stu274, Stu282, Stu283, Stu293, Stu298, Stu314, Stu324, Stu328, Stu370, Stu372, Stu385, Stu394, Stu414, Stu418, Stu438, Stu454, Stu470);
        SUPACollegeItalian_507020.add(Stu42, Stu94, Stu157, Stu165, Stu265, Stu302, Stu356, Stu446);
        SUPACollegeSpanish_507010.add(Stu27, Stu52, Stu64, Stu70, Stu115, Stu136, Stu147, Stu171, Stu180, Stu183, Stu197, Stu208, Stu267, Stu320, Stu330, Stu340, Stu359, Stu377, Stu387, Stu402, Stu420, Stu430);
        TacticalSports_801063.add(Stu1, Stu2, Stu16, Stu20, Stu22, Stu30, Stu46, Stu53, Stu61, Stu79, Stu90, Stu114, Stu118, Stu120, Stu125, Stu133, Stu137, Stu142, Stu150, Stu162, Stu166, Stu172, Stu174, Stu181, Stu198, Stu199, Stu209, Stu210, Stu212, Stu235, Stu240, Stu242, Stu243, Stu244, Stu261, Stu266, Stu277, Stu289, Stu294, Stu295, Stu296, Stu305, Stu312, Stu319, Stu324, Stu325, Stu327, Stu331, Stu338, Stu339, Stu345, Stu346, Stu353, Stu359, Stu363, Stu368, Stu369, Stu374, Stu378, Stu383, Stu388, Stu389, Stu390, Stu394, Stu401, Stu419, Stu420, Stu421, Stu423, Stu467);
        TheaterArtsIIPerformanceWorkshop_620203.add(Stu33, Stu60, Stu85, Stu91, Stu163, Stu193, Stu218, Stu242, Stu244, Stu250, Stu253, Stu285, Stu304, Stu320, Stu397);
        TopicsinAlgebraII_201110.add(Stu32, Stu49, Stu91, Stu92, Stu150, Stu199, Stu207, Stu221, Stu305, Stu352, Stu438, Stu439);
        TopicsinAppliedMath_201160.add(Stu14, Stu24, Stu37, Stu42, Stu82, Stu94, Stu118, Stu173, Stu226, Stu264, Stu296, Stu302, Stu308, Stu356, Stu372);
        UnitedStatesHistory_401030.add(Stu5, Stu6, Stu25, Stu29, Stu31, Stu32, Stu46, Stu49, Stu55, Stu73, Stu78, Stu79, Stu86, Stu92, Stu98, Stu106, Stu122, Stu129, Stu152, Stu155, Stu160, Stu172, Stu175, Stu185, Stu190, Stu202, Stu207, Stu213, Stu216, Stu221, Stu237, Stu240, Stu246, Stu247, Stu249, Stu251, Stu252, Stu254, Stu256, Stu268, Stu279, Stu283, Stu289, Stu299, Stu303, Stu305, Stu329, Stu351, Stu354, Stu355, Stu370, Stu371, Stu374, Stu394, Stu403, Stu413, Stu423, Stu424, Stu436, Stu437, Stu439, Stu452, Stu454, Stu457, Stu460);
        UnitedStatesHistory_401030I.add(Stu75, Stu91, Stu120, Stu137, Stu199, Stu227, Stu250, Stu352, Stu409);
        WindEnsemble_631103.add(Stu63, Stu69, Stu85, Stu111, Stu130, Stu177, Stu198, Stu205, Stu215, Stu236, Stu258, Stu285, Stu294, Stu343, Stu350, Stu375, Stu435, Stu462);

        x.add(Chorus_621010, 3, 4);//period 2
        x.add(CollegeChorus_627010, 3, 4);//period 2
        x.add(ChorusABDay_621013, 3);//   2A
        x.add(CollegeWindEnsemble_637010, 13, 14);// Period 7
        x.add(HonorsWindEnsemble_638100, 13, 14);//Period 7
        x.add(WindEnsemble_631103, 13);//    Period 7A
        x.add(ConcertBand_631010, 7, 8);// Period 4
        x.add(ConcertBand_631013, 8);  //4B
        x.add(HonorsShapersoftheWorld_108010, 15, 16); //Period 8
        x.add(MusicProduction_631073, 3);//2A
        x.add(MusicProduction_631073, 12);//6B
        x.add(AdvancedArt_611040, 5, 6);// Period 3
        x.add(PhotographyPortfolio_611100, 5, 6); //Period 3
        x.add(PhotographyPortfolio_611103, 5);  //3A
        x.add(PhotographyPortfolio_611103, 6);  //3B
        x.add(AdvancedPhotoPortfolio_611130, 5, 6);// Period 3
        x.add(AP2DDesign_619110, 5, 6);// Period 3
        x.add(PortfolioDevelopment_611050, 5, 6);
        x.add(PortfolioDevelopment2_611060, 5, 6);
        x.add(APDrawing_Painting_619060, 5, 6);

        x.add(SUPACollegeItalian_507020, 7, 8);
        x.add(APItalianLanguage_Culture_509190, 7, 8);
        x.add(Calculus_201120, 13, 14);
        x.add(TopicsinAppliedMath_201160, 13, 14);// Period 7
        x.add(APCalculusBC_209140, 5, 6);// Period 3
        x.add(APStatistics_209120, 1, 2);// Period 1
        x.add(ScienceandSociety_301120, 1, 2);//Period 1
        x.add(APGovt_Politics_409100, 11, 12); //Period 6
        x.add(HonorsItalian4_508180, 1, 2);// Period 1
        x.add(Italian4_501180, 11, 12);// Period 6
        x.add(TopicsinAlgebraII_201110, 7, 8);//Period 4
        x.add(APComputerScience_729010, 7, 8);
        x.add(HonorsItalian3_508170, 15, 16);
        x.add(Italian3_501170, 13, 14);
        x.add(EarthSciencewlab_PH301150, 1, 2, 4);//Period 1 and 2B
        x.add(APPhysics1wlab_PH309030, 7, 8, 9);//Period 4 and 5A
        x.add(APPhysics1wlab_PH309030, 14, 15, 16);//Period 7B and 8
        x.add(APBiologywlab_PH309020, 3, 5, 6);//Period 2A and 3
        x.add(APBiologywlab_PH309020, 14, 15, 16);//Period 7B and 8
        x.add(Physicswlab_PH301090, 1, 2, 4);//Period 1 and 2A now 1 and2B
        x.add(Physicswlab_PH301090, 3, 5, 6);//Period 2B and 3, now 2A and 3
        x.add(Physicswlab_PH301090, 10, 11, 12);//Period 5B and 6 trying 5A and 6
        x.add(ForensicScience_301130i, 9, 10);//Period 5
        x.add(ForensicScience_301130, 9, 10);//Period 5
        x.add(ForensicScience_301130, 15, 16);//Period 8
        x.add(HonorsEngineeringDesign_728020, 11, 12);//Period 6
        x.add(APSpanishLanguage_Culture_509140, 3, 4);//Period 2
        x.add(APSpanishLanguage_Culture_509140, 7, 8);//Period 4
        x.add(APCalculusAB_209130, 1, 2);//Period 1
        x.add(APCalculusAB_209130, 9, 10);//Period 5
        x.add(APEnglish12LitandComp_109050, 5, 6);//Period 6//trying out 3
        x.add(APEnglish12LitandComp_109050, 15, 16);//Period 8
        x.add(APMacroeconomics_409090, 7, 8);//Period 4
        x.add(APMacroeconomics_409090, 11, 12);//Period 6

        x.add(HonorsPreCalculus_208110, 9, 10);//Period 5
        x.add(HonorsPreCalculus_208110, 15, 16);//Period 8
        x.add(HonorsPreCalculus_208110, 13, 14);//Period 7
        x.add(HonorsPreCalculus_208110, 5, 6);//Period 3
        x.add(HonorsComputerScience_728010, 3, 4);//Period 2
        x.add(HonorsComputerScience_728010, 9, 10);//Moved to period 6, confirm that it should stay
        x.add(Economics_401053I, 12);
        x.add(ConstitutionalLaw_401083I, 11);
        x.add(Economics_401053, 3);
        x.add(Economics_401053, 12);
        x.add(Economics_401053, 13);
        x.add(SUPACollegeSpanish_507010, 5, 6);
        x.add(SUPACollegeSpanish_507010, 15, 16);
        x.add(ConstitutionalLaw_401083, 4);
        x.add(ConstitutionalLaw_401083, 11);
        x.add(ConstitutionalLaw_401083, 15);
        x.add(ContemporaryIssues_401093, 14);/*14 16,  789 141516, 78 -- Contemp Issues 14 16 both get hit by APPhysics
                       causing a 3-way conflict AP Comp Sci, AP Physics, Contemp Issues*/
        x.add(ContemporaryIssues_401093, 16);
        x.add(ComparativeLiterature_101050I, 9, 10);
        x.add(ComparativeLiterature2_101050i2, 7, 8);
        x.add(ComparativeLiterature_101050, 7, 8);
        x.add(ComparativeLiterature_101050, 9, 10);
        x.add(ComparativeLiterature_101050, 13, 14);
        x.add(LatinI_501200, 1, 2);
        x.add(Spanish4_501100, 1, 2);
        x.add(Spanish4_501100, 11, 12);
        x.add(Spanish3_501090, 9, 10);

        x.add(HonorsSpanish4_508110, 1, 2);
        x.add(HonorsSpanish4_508110, 7, 8);
        x.add(HonorsSpanish4_508110, 11, 12);

        x.add(AlgebraIITrigonometry_201080, 5, 6);
        x.add(AlgebraIITrigonometry_201080, 13, 14);

        x.add(APPsychology_309120, 1, 2);
        x.add(APPsychology_309120, 9, 10);
        x.add(APPsychology_309120, 11, 12);
        x.add(APPsychology_409120, 1, 2);
        x.add(APPsychology_409120, 9, 10);
        x.add(APPsychology_409120, 11, 12);

        x.add(LearningCenter12_921004, 3, 4);
        x.add(LearningCenter12_921004, 9, 10);
        x.add(LearningCenter12_921004, 15, 16);

        x.add(LearningCenter12_921043, 3);
        x.add(LearningCenter12_921043, 4);
        x.add(LearningCenter12_921043, 9);
        x.add(LearningCenter12_921043, 10);
        x.add(LearningCenter12_921043, 15);
        x.add(LearningCenter12_921043, 16);

        x.add(PreCalculus_201100, 5, 6);
        x.add(PreCalculus_201100, 7, 8);
        x.add(PreCalculus_201100, 13, 14);

        x.add(CollegeMarketing_717030, 1, 2);
        x.add(CollegeMarketing_717030, 5, 6);

        x.add(APEnglish11LangandComp_109060, 1, 2);
        x.add(APEnglish11LangandComp_109060, 9, 10);//trying out 5, was 3
        x.add(APEnglish11LangandComp_109060, 13, 14);

        x.add(English11_101040I, 15, 16);
        x.add(English11_101040, 3, 4);
        x.add(English11_101040, 9, 10);
        x.add(English11_101040, 15, 16);

        x.add(UnitedStatesHistory_401030I, 1, 2);
        x.add(UnitedStatesHistory_401030, 1, 2);
        x.add(UnitedStatesHistory_401030, 3, 4);
        x.add(UnitedStatesHistory_401030, 7, 8);

        x.add(APUSHistory_409040, 5, 6);
        x.add(APUSHistory_409040, 11, 12);
        x.add(APUSHistory_409040, 15, 16);

        x.add(LearningCenter11_921003, 3, 4);
        x.add(LearningCenter11_921003, 5, 6);
        x.add(LearningCenter11_921003, 13, 14);
        x.add(LearningCenter11_921033, 3);
        x.add(LearningCenter11_921033, 4);
        x.add(LearningCenter11_921033, 5);
        x.add(LearningCenter11_921033, 6);
        x.add(LearningCenter11_921033, 13);
        x.add(LearningCenter11_921033, 14);

        x.add(HonorsChemistrywlab_PH308060, 1, 2, 3);
        x.add(HonorsChemistrywlab_PH308060, 7, 8, 9);

        x.add(HonorsSpanish3_508160, 11, 12);
        x.add(HonorsSpanish3_508160, 15, 16);

        x.add(HonorsScienceReseach_308110, 10);
        x.add(HonorsScienceReseach_308110, 4);

        x.add(AppliedChemistry_301040, 15, 16);

        x.add(Chemistrywlab_PH301050, 4, 5, 6);
        x.add(Chemistrywlab_PH301050, 7, 9, 10);
        x.add(Chemistrywlab_PH301050, 15, 16, 1);

        x.add(Health_881063, 2);
        x.add(Health_881063, 3);
        x.add(Health_881063, 4);
        x.add(Health_881063, 8);
        x.add(Health_881063, 10);

        x.add(StudioinArtII_611033, 4);
        x.add(StudioinArtII_611033, 13);

        x.add(PhotoII_611083, 3);
        x.add(PhotoII_611083, 14);

        x.add(DigitalPhotography_611093, 9);
        x.add(DigitalPhotography_611093, 13);

        x.add(Geometry_201050, 7, 8);
        x.add(Geometry_201050, 11, 12);
        x.add(GeometryB_201040, 5, 6);

        x.add(AlgebraIYr2_201210, 1, 2);

//        x.add(IntroductionToPsychology_401070, 7, 8);

        x.add(HonorsAlgebraIITrigonometry_208090, 1, 2);
        x.add(HonorsAlgebraIITrigonometry_208090, 5, 6);
        x.add(HonorsAlgebraIITrigonometry_208090, 15, 16);

        x.add(English10_101030, 1, 2);
        x.add(English10_101030, 5, 6);
        x.add(English10_101030, 7, 8);
        x.add(English10_101030, 11, 12);
        x.add(English10_101030, 3, 4);
        x.add(English10_101030I, 7, 8);

        x.add(LearningCenter10_921002, 9, 10);
        x.add(LearningCenter10_921002, 15, 16);
        x.add(LearningCenter10_921023, 9);
        x.add(LearningCenter10_921023, 10);
        x.add(LearningCenter10_921023, 15);
        x.add(LearningCenter10_921023, 16);

        x.add(GlobalHistory_Geography10_401020I, 11, 12);

        x.add(GlobalHistory_Geography10_401020, 5, 6);
        x.add(GlobalHistory_Geography10_401020, 9, 10);
        x.add(GlobalHistory_Geography10_401020, 11, 12);
        x.add(GlobalHistory_Geography10_401020, 13, 14);
        x.add(GlobalHistory_Geography10_401020, 15, 16);


        x.add(TacticalSports_801063, 3);
        x.add(TacticalSports_801063, 9);
        x.add(TacticalSports_801063, 14);
        //
        x.add(PersonalFitness_881053, 4);
        x.add(PersonalFitness_881053, 13);
        x.add(PhysicalEducation10_12_801023, 3);
        x.add(PhysicalEducation10_12_801023, 4);
        x.add(PhysicalEducation10_12_801023, 7);
        x.add(PhysicalEducation10_12_801023, 10);
        x.add(PhysicalEducation10_12_801023, 13);
        x.add(PhysicalEducation10_12_801023, 14);
        x.add(PhysicalEducation10_12_801023, 15);
        x.add(PhysicalEducation10_12_801023, 16);
        x.add(English9_101010, 15, 16, 2);
        x.add(English9_101010, 3, 4, 5);
        x.add(English9_101010, 3, 4, 6);
        x.add(English9_101010, 7, 9, 10);
        x.add(English9_101010, 9, 10, 12);
        x.add(English9_101010, 13, 14, 16);
        x.add(English9_101010I, 3, 4, 6);
        x.add(English9_101010, 13, 14, 16);

        x.add(PhysicalEducation9_801013, 1);
        x.add(PhysicalEducation9_801013, 5);
        x.add(PhysicalEducation9_801013, 6);
        x.add(PhysicalEducation9_801013, 8);
        x.add(PhysicalEducation9_801013, 11);
        x.add(PhysicalEducation9_801013, 15);

        x.add(BiologywithLAB_PH301010, 1, 2, 3);//could be 1,2,4, swapping labs with other sect
        x.add(BiologywithLAB_PH301010, 4, 5, 6);
        x.add(BiologywithLAB_PH301010, 5, 6, 7);
        x.add(BiologywithLAB_PH301010, 7, 8, 10);
        x.add(BiologywithLAB_PH301010, 11, 12, 14);
        x.add(BiologywithLAB_PH301010, 13, 15, 16);
        x.add(BiologywithLab_PH30101I, 7, 8, 10);
        x.add(IntrotoHumanitiesResearch_101033s, 9);
        x.add(AlgebrawLab_201010I, 9, 11, 12);
        x.add(AlgebrawLab_201010, 9, 11, 12);
//        x.add(Robotics_721063, 4);
        x.add(IntrotoScienceResearch_301033, 14);
        x.add(Italian2_501160, 5, 6);
        x.add(Italian2_501160, 13, 14);  // moved 8 to 7
        x.add(HonorsGeometry_208060, 1, 2);
        x.add(HonorsGeometry_208060, 11, 12);
        x.add(Photography_611070, 1, 2);
        x.add(Photography_611070, 7, 8);
        x.add(AlgebraI_201020, 3, 4);  //moved 1 to 2
        x.add(AlgebraI_201020, 15, 16); //moved 7 to 8
        x.add(AlgebraIYr1_201021, 11, 12);
        x.add(GlobalHistory_Geography9_401010I, 1, 2);
        x.add(LearningCenter9_921013);
        x.add(LearningCenter9_921001, 1, 2);
        x.add(LearningCenter9_921001, 15, 16);
        x.add(LearningCenter9_921013, 1);
        x.add(LearningCenter9_921013, 2);
        x.add(LearningCenter9_921013, 15);
        x.add(LearningCenter9_921013, 16);
        x.add(English9_101010S, 13, 14);
        x.add(English10_101030S, 13, 14);

        x.add(Accounting_711010, 7, 8);
        x.add(Accounting_711010, 13, 14);
        x.add(CollegeAccounting_717020, 9, 10);
        x.add(CollegeAccounting_717020, 15, 16);
        x.add(TheaterArtsIIPerformanceWorkshop_620203, 14);

        x.add(StudioinArt_611010, 1, 2);
        x.add(StudioinArt_611010, 9, 10);
        x.add(StudioinArt_611010, 11, 12);
        x.add(StudioinArt_611010, 15, 16);

        x.add(Spanish2_501080, 5, 6);
        x.add(Spanish2_501080, 13, 14);
        x.add(Spanish2_501080, 11, 12);//was 6th, trying 8th

        x.add(GlobalHistory_Geography9_401010, 1, 2);
        x.add(GlobalHistory_Geography9_401010, 3, 4);
        x.add(GlobalHistory_Geography9_401010, 7, 8);
        x.add(GlobalHistory_Geography9_401010, 9, 10);
        x.add(GlobalHistory_Geography9_401010, 15, 16);
        x.add(Spanish1_501070, 13, 14);

        //Accounting_711010
        //AdvancedArt_611040
        //AdvancedPhotoPortfolio_611130
        //AlgebraI_201020
        //AlgebraIITrigonometry_201080
        //AlgebraIYr1_201021
        //AlgebraIYr2_201210
        //AlgebrawLab_201010
        //AlgebrawLab_201010I
        //AP2DDesign_619110
        //APBiologywlab_PH309020
        //APCalculusAB_209130
        //APCalculusBC_209140
        //APComputerScience_729010
        //APDrawing_Painting_619060
        //APEnglish11LangandComp_109060
        //APEnglish12LitandComp_109050
        //APGovt_Politics_409100
        //APItalianLanguage_Culture_509190
        //APMacroeconomics_409090
        //APPhysics1wlab_PH309030
        //AppliedChemistry_301040
        //APPsychology_309120
        //APPsychology_409120
        //APSpanishLanguage_Culture_509140
        //APStatistics_209120
        //APUSHistory_409040
        //BiologywithLAB_PH301010
        //BiologywithLab_PH30101I
        //Calculus_201120
        //Chemistrywlab_PH301050
        //Chorus_621010
        //ChorusABDay_621013
        //CollegeAccounting_717020
        //CollegeChorus_627010
        //CollegeMarketing_717030
        //CollegeWindEnsemble_637010
        //ComparativeLiterature_101050
        //ComparativeLiterature_101050I
        //ComparativeLiterature2_101050i2
        //ConcertBand_631010
        //ConcertBand_631013
        //ConstitutionalLaw_401083
        //ConstitutionalLaw_401083I
        //ContemporaryIssues_401093
        //DigitalPhotography_611093
        //EarthSciencewlab_PH301150
        //Economics_401053
        //Economics_401053I
        //English10_101030
        //English10_101030I
        //English10_101030S
        //English11_101040
        //English11_101040I
        //English9_101010
        //English9_101010I
        //English9_101010S
        //ESL_101011
        //ForensicScience_301130
        //ForensicScience_301130i
        //Geometry_201050
        //GeometryB_201040
        //GlobalHistory_Geography10_401020
        //GlobalHistory_Geography10_401020I
        //GlobalHistory_Geography9_401010
        //GlobalHistory_Geography9_401010I
        //Health_881063
        //HonorsAlgebraIITrigonometry_208090
        //HonorsChemistrywlab_PH308060
        //HonorsComputerScience_728010
        //HonorsEngineeringDesign_728020
        //HonorsGeometry_208060
        //HonorsItalian3_508170
        //HonorsItalian4_508180
        //HonorsPreCalculus_208110
        //HonorsScienceReseach_308110
        //HonorsShapersoftheWorld_108010
        //HonorsSpanish3_508160
        //HonorsSpanish4_508110
        //HonorsWindEnsemble_638100
        //IntroductionToPsychology_401070
        //IntrotoHumanitiesResearch_101033
        //IntrotoHumanitiesResearch_101033i
        //IntrotoHumanitiesResearch_101033s
        //IntrotoScienceResearch_301033
        //Italian2_501160
        //Italian3_501170
        //Italian4_501180
        //LatinI_501200
        //LearningCenter10_921002
        //LearningCenter10_921023
        //LearningCenter11_921003
        //LearningCenter11_921033
        //LearningCenter12_921004
        //LearningCenter12_921043
        //LearningCenter9_921001
        //LearningCenter9_921013
        //MusicProduction_631073
        //PersonalFitness_881053
        //Photography_611070
        //PhotographyPortfolio_611100
        //PhotographyPortfolio_611103
        //PhotoII_611083
        //PhysicalEducation10_12_801023
        //PhysicalEducation9_801013
        //Physicswlab_PH301090
        //PortfolioDevelopment_611050
        //PortfolioDevelopment2_611060
        //PreCalculus_201100
        //Robotics_721063
        //ScienceandSociety_301120
        //Spanish1_501070
        //Spanish2_501080
        //Spanish3_501090
        //Spanish4_501100
        //StudioinArt_611010
        //StudioinArtII_611033
        //SUPACollegeItalian_507020
        //SUPACollegeSpanish_507010
        //TacticalSports_801063
        //TheaterArtsIIPerformanceWorkshop_620203
        //TopicsinAlgebraII_201110
        //TopicsinAppliedMath_201160
        //UnitedStatesHistory_401030
        //UnitedStatesHistory_401030I
        //WindEnsemble_631103


        //System.out.println(PersonalFitness_881053.roster.size());

//        Course A = new Course("A", "1", 2, 0.5);//2 sections
//        Course B = new Course("B", "1", 2);//2 sections
//        Course C = new Course("C", "1234", 3);//2 sections
//        Course CI = new Course("CI","1234I",1);
//        Course D = new Course ("D","22",1);
//        Student s1 = new Student("s1", 9);
//        Student s2 = new Student("s2", 9);
//        Student s3 = new Student("s3", 9);
//        Student s4 = new Student("s4", 9);
//        Student s5 = new Student("s5", 9);
//        Student s6 = new Student("s6", 9);
//        Student s7 = new Student("s7", 9);
//        Student s8 = new Student("s8", 9);
//        Student s9 = new Student("s9", 9);
//        A.add(s2, s3);//                 A: (3) 1.5, (5) 1.5
//        B.add(s3, s4, s5, s6);//          x  B: (3,4) 2.0 (7,8) 2.0
//        C.add(s1, s3, s5,s8, s9);//       x  C: (3,4) 3.0 (11,12),3.0  (15,16) 3.0
//        CI.add(s4,s6,s7);//              CI:(11,12)
//        D.add(s1,s3,s5);					//use period (3,4) s1 - 1, s3 - 2, s5 - 2
//
//        x.add(A, 3);
//        x.add(A,3);
//        x.add(A, 5);
//        x.add(B, 3, 4);
//        x.add(B, 7, 8);
//        x.add(B,7,8);
//        x.add(C, 11, 12);
//        x.add(C, 11, 12);
//        x.add(C, 15, 16);
//        x.add(C, 11, 12);
//        x.add(C,3,4);
//        x.add(C, 11, 12);
//        x.add(CI, 11,12);


        x.check(IntroductionToPsychology_401070, Robotics_721063);

        //       x.seatCount(9);
        //        x.seatCount(10);
        //        x.seatCount(11);
        //        x.seatCount(12);

        //Course LearningCenter9_921013 = new Course("LearningCenter9","921013",2,0.5);

    }
}

