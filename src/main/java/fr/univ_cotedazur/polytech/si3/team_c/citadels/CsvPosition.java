package fr.univ_cotedazur.polytech.si3.team_c.citadels;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to set the column position in CSV file
 *
 * @author Team C
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvPosition {
    int position() default 0;
}
