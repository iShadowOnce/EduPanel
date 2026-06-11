package com.edupanel.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    private String id;
    private String nombre;
    private List<String> alumnosIds = new ArrayList<>();

}
