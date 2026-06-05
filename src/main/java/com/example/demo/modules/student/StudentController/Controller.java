
package com.example.demo.modules.student.StudentController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.modules.student.entity.Student;
import com.example.demo.modules.student.server.StudentServer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/student")
public class Controller {

    @Resource
    private StudentServer studentService;

    //1.新增 POST http://localhost:8080/student
    @PostMapping
    public String add(@RequestBody Student student){
        studentService.save(student);
        log.info("新增成功");
        return "新增成功";
    }

    //2.根据id查询 GET /student/{id}
    @GetMapping("/{id}")
    public Student getById(@PathVariable Integer id){
        log.info("查询成功");
        return studentService.getById(id);
    }

    //3.查询全部 GET /student/list
    @GetMapping("/list")
    public List<Student> listAll(){
//        throw new RuntimeException("查询失败");
        return studentService.list();
 }

    //4.分页查询 GET /student/page?pageNum=1&pageSize=3
    @GetMapping("/page")
    public IPage<Student> page(@RequestParam(defaultValue = "1") Long pageNum,
                               @RequestParam(defaultValue = "3") Long pageSize){
        Page<Student> page = new Page<>(pageNum,pageSize);
        return studentService.page(page,null);
    }

    //5.修改 PUT /student
    @PutMapping
    public String update(@RequestBody Student student){
        studentService.updateById(student);
        return "修改成功";
    }

    //6.删除 DELETE /student/{id}
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id){
        studentService.removeById(id);
//        return "删除成功";
          throw  new RuntimeException("删除失败");
    }

}