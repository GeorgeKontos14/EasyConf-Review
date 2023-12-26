package nl.tudelft.sem.template.example.domain.models;


import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Entity
public class Comment {

    //Placeholder class until the actual class is implemented
    @Id
    private Integer id;

    public void setId(Integer id) {
        this.id = id;
    }

}
