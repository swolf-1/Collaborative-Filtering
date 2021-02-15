package com.company;

import java.security.SecureRandom;
import java.util.Random;

public class Main {

    private static final int N=100;
    private static final int M=100;
    private static final int T=10;
    private static final int K=3;

    /*
    Παρακάτω μπορείτε να δείτε τον κώδικα μου με σχόλιο σχεδον για την καθε λειτουργία, επισης μπορειτε να χρησιμοποιήσετε
    την συνάρτηση print2DArray για να τυπώνετε τους πινακες και να βλεπετς τα αποτελέσματα και στις γραμμες 43 και 83 αν βγαλετε
    τα σχόλια θα δειτε τον αρχικό πινακα με τις βαθμολογίες (ολες) και τον πινακα που προκύπτει μετά την επιλογή του ποσοστού.
     */



    public static void main(String[] args) {


        int percentage_of_known_grades,number_of_known_grades,number_of_unknown_grades,rounds=0;

        //Our grades table
        double[][] complete_table = new double[N][M];

        //Random numbers generator
        Random random = new SecureRandom();

        //Put random numbers in the grades table
        for(int i = 0; i < N; i++)
        {
            for (int j = 0; j < M; j++)
            {
                double random_number = 1.0 + random.nextDouble() * (10.0 - 1.0);
                random_number = Math.round(random_number *100.0)/100.0;
                complete_table[i][j] = random_number;
            }
        }

        /*
        System.out.println("---The original table with the grades(fully complete)---");
        print2DArray(complete_table);
         */

        double average_for_all_Jaccard_absErr = 0.00;
        double average_for_all_Dice_absErr = 0.00;
        double average_for_all_Cosine_absErr = 0.00;
        double average_for_all_Adjusted_Cosine_absErr = 0.00;

        while(rounds < T) {

            System.out.println("Repeat number:"+ (rounds+1));

            //Getting random percentage of known grades
            percentage_of_known_grades = random.nextInt(100 - 5) + 5;
            System.out.println("The percentage of known grades is:"+percentage_of_known_grades + "%");

            //Calculating number of known grades
            number_of_known_grades = N * M * percentage_of_known_grades / 100;

            //Calculating number of unknown grades
            number_of_unknown_grades = N * M - number_of_known_grades;

            //Table we will work on(filled only with the percentage we randomly generated)
            double[][] after_table = new double[N][M];
            int n_random, m_random;

            for (int i = 0; i < number_of_known_grades; i++) {
                n_random = random.nextInt(N);
                m_random = random.nextInt(M);

                if (after_table[n_random][m_random] != 0.00) {
                    do {
                        n_random = random.nextInt(N);
                        m_random = random.nextInt(M);
                    } while (after_table[n_random][m_random] != 0.00);

                }
                after_table[n_random][m_random] = complete_table[n_random][m_random];
            }
/*
            System.out.println("---The table on witch we will work on (filled only with the " + percentage_of_known_grades + "% of grades)---");
            print2DArray(after_table);

 */
            //This will be the table where we will fill the (100-X)% of grades using Jaccard Similarity.
            double[][] jaccard_after_table = new double[N][M];

            //This will be the table where we will fill the (100-X)% of grades using Dice Similarity.
            double[][] dice_after_table = new double[N][M];

            //This will be the table where we will fill the (100-X)% of grades using Cosine Similarity.
            double[][] cosine_after_table = new double[N][M];

            //This will be the table where we will fill the (100-X)% of grades using Adjusted Cosine Similarity.
            double[][] adjusted_cosine_after_table = new double[N][M];


            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    jaccard_after_table[i][j] = after_table[i][j];
                    dice_after_table[i][j] = after_table[i][j];
                    cosine_after_table[i][j] = after_table[i][j];
                    adjusted_cosine_after_table[i][j] = after_table[i][j];
                }
            }

            double absAverageErrorJaccard = 0.00;

            //The fill using Jaccard Similarity.
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (jaccard_after_table[i][j] == 0.00) {
                        jaccard_after_table[i][j] = jaccard(j, i, jaccard_after_table, K);

                        absAverageErrorJaccard += Math.abs(jaccard_after_table[i][j] - complete_table[i][j]);

                    }
                }
            }

            absAverageErrorJaccard /= number_of_unknown_grades;
            System.out.println("Average error Jaccard:" + absAverageErrorJaccard);


            double absAverageErrorDice = 0.00;

            //The fill using Dice Similarity.
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (dice_after_table[i][j] == 0.00) {
                        dice_after_table[i][j] = dice(j, i, dice_after_table, K);

                        absAverageErrorDice += Math.abs(dice_after_table[i][j] - complete_table[i][j]);
                    }
                }
            }

            absAverageErrorDice /= number_of_unknown_grades;
            System.out.println("Average error Dice:" + absAverageErrorDice);


            double absAverageErrorCosine = 0.00;

            //The fill using Cosine Similarity.
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (cosine_after_table[i][j] == 0.00) {
                        cosine_after_table[i][j] = cosine(j, i, cosine_after_table, K);

                        absAverageErrorCosine += Math.abs(cosine_after_table[i][j] - complete_table[i][j]);
                    }
                }
            }

            absAverageErrorCosine /= number_of_unknown_grades;
            System.out.println("Average error Cosine:" + absAverageErrorCosine);


            //Normalizing table for Adjusted Cosine
            adjust_cosine(adjusted_cosine_after_table);

            double absAverageErrorAdjustedCosine = 0.00;

            //The fill using Cosine Similarity but on the Normalized table(Adjusted Cosine Similarity)
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (adjusted_cosine_after_table[i][j] == 0.00) {
                        adjusted_cosine_after_table[i][j] = cosine(j, i, cosine_after_table, K);

                        absAverageErrorAdjustedCosine += Math.abs(adjusted_cosine_after_table[i][j] - complete_table[i][j]);
                    }
                }
            }

            absAverageErrorAdjustedCosine /= number_of_unknown_grades;
            System.out.println("Average error Adjusted Cosine:" + absAverageErrorAdjustedCosine);

            rounds++;

            System.out.println("-------------------------");


                average_for_all_Jaccard_absErr += absAverageErrorJaccard;
                average_for_all_Dice_absErr += absAverageErrorDice;
                average_for_all_Cosine_absErr += absAverageErrorCosine;
                average_for_all_Adjusted_Cosine_absErr += absAverageErrorAdjustedCosine;

        }

        System.out.println("-------------------------");
        System.out.println("For "+T+ " repeats the average of average absolute error for each method is:");
        System.out.println("For Jaccard:" + average_for_all_Jaccard_absErr / T);
        System.out.println("For Dice:" + average_for_all_Dice_absErr / T);
        System.out.println("For Cosine:" + average_for_all_Cosine_absErr / T);
        System.out.println("For Adjusted Cosine:" + average_for_all_Adjusted_Cosine_absErr / T);

    }

    public static void adjust_cosine(double[][] table)
    {
        double total_user_grade = 0.00;
        int graded = 0;

        for (int i = 0; i < N; i ++)
        {
            for (int j = 0; j < N; j ++)
            {
                if (table[i][j] != 0.00)
                {
                    graded++;
                    total_user_grade+= table[i][j];
                }
            }

            for (int j = 0; j < N; j ++)
            {
                if (table[i][j] != 0.00)
                {
                    table[i][j] -=  total_user_grade/graded;
                }
            }

            total_user_grade = 0.00;
            graded = 0;

        }

    }

    public static double cosine(int product, int user, double[][] table,int k)
    {
        double square_product=0,square_product1=0,numerator =0;
        double[] table_with_cosine_similarity = new double[M];



        for (int j = 0; j < M; j++)
        {
            if (j != product)
            {
                for (int i = 0; i < N; i++)
                {
                    numerator += table[i][product]*table[i][j];
                    square_product += Math.pow(table[i][product],2);
                    square_product1 += Math.pow(table[i][j],2);
                }

                square_product = Math.sqrt(square_product);
                square_product1 = Math.sqrt(square_product1);

                table_with_cosine_similarity[j] = numerator / (square_product*square_product1);

                square_product = 0;
                square_product1 = 0;
                numerator = 0;
            }
        }

        double max,predicted_grade,final_numerator = 0,denominator = 0;
        int max_i,round=0;

        while(k != 0 && round < M)
        {

            max = table_with_cosine_similarity[0];
            max_i = 0;

            for (int i = 1; i < table_with_cosine_similarity.length; i++)
            {
                if (table_with_cosine_similarity[i] > max)
                {
                    max = table_with_cosine_similarity[i];
                    max_i = i;
                }
            }

            if (table[user][max_i] != 0.00)
            {
                final_numerator += max * table[user][max_i];
                denominator += max;
                k--;
            }

            table_with_cosine_similarity[max_i] = 0.0;
            round++;
        }

        predicted_grade = final_numerator / denominator;

        return predicted_grade;
    }

    public static double dice(int product, int user, double[][] table,int k)
    {
        int total_product = 0, total_product1=0, common =0;
        double[] table_with_dice_similarity = new double[M];



        for (int j = 0; j < M; j++)
        {
            if (j != product)
            {
                for (int i = 0; i < N; i++)
                {
                    if (table[i][product] != 0.00)
                    {
                        total_product++;
                    }

                    if (table[i][j] != 0.00)
                    {
                        total_product1++;
                    }

                    if (table[i][product] != 0.00 && table[i][j] != 0.00)
                    {
                        common++;
                    }
                }

                table_with_dice_similarity[j] = (double) (2 * common) / (total_product + total_product1);
                total_product = 0;
                total_product1 = 0;
                common = 0;
            }
        }

        double max,predicted_grade,numerator = 0,denominator = 0;
        int max_i,round=0;

        while(k != 0 && round < M)
        {

            max = table_with_dice_similarity[0];
            max_i = 0;

            for (int i = 1; i < table_with_dice_similarity.length; i++)
            {
                if (table_with_dice_similarity[i] > max)
                {
                    max = table_with_dice_similarity[i];
                    max_i = i;
                }
            }

            if (table[user][max_i] != 0.00)
            {
                numerator += max * table[user][max_i];
                denominator += max;
                k--;
            }

            table_with_dice_similarity[max_i] = 0.0;
            round++;
        }

        predicted_grade = numerator / denominator;

        return predicted_grade;

    }

    public static double jaccard(int product, int user, double[][] table,int k)
    {
        int total_in_common=0 , section=0;
        double[] table_with_jaccard_similarity = new double[M];

        for (int j = 0; j < M; j++)
        {
            if (j != product)
            {
                for (int i = 0; i < N; i++)
                {
                    if (table[i][product] != 0.00 || table[i][j] != 0.00)
                    {
                        section++;
                    }

                    if (table[i][product] != 0.00 && table[i][j] != 0.00)
                    {
                        total_in_common++;
                    }
                }

                table_with_jaccard_similarity[j] = (double) total_in_common/section;
                total_in_common=0;
                section=0;
            }
        }

        double max,predicted_grade,numerator = 0,denominator = 0;
        int max_i,round=0;

        while(k != 0 && round < M)
        {

            max = table_with_jaccard_similarity[0];
            max_i = 0;

            for (int i = 1; i < table_with_jaccard_similarity.length; i++)
            {
                if (table_with_jaccard_similarity[i] > max)
                {
                    max = table_with_jaccard_similarity[i];
                    max_i = i;
                }
            }

            if (table[user][max_i] != 0.00)
            {
                numerator += max * table[user][max_i];
                denominator += max;
                k--;
            }

            table_with_jaccard_similarity[max_i] = 0.0;
            round++;
        }



        predicted_grade = numerator / denominator;

        return predicted_grade;


    }

    public static void print2DArray(double[][] array)
    {
        for (int i = 0; i < N; i++)
        {
            System.out.format("%2d |", i);
            for (int j = 0; j < M; j++)
            {
                System.out.format("%.2f|",array[i][j]);
            }
            System.out.println();
        }
    }
}
