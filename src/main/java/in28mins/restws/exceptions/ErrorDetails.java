package in28mins.restws.exceptions;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails
{
    private Timestamp ts;
    private String message;
    private String path;
    private String details;

}
