package com.example.demo.modules.student.server.impl;

import com.example.demo.modules.student.entity.Student;
import com.example.demo.modules.student.mapper.StudentMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.modules.student.server.StudentServer;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentServer {
}
