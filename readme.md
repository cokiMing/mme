##欢迎使用mybatis-mysql-extends

####本项目适用于基于mybatis注解实现的单表查询，以下是使用说明

#####1.查询

```
    //类必须继承本项目中的BaseService类
    public class StudentService extends BaseService<Student,StudentMapper>{
        
        //根据名字和年龄大小查找学生信息
        public List<Student> findByNameAndAgeGreaterThan(String name,int age) {
            SqlQuery sqlQuery = SqlQuery.newInstant();
            sqlQuery.field("name").equal(name);
            sqlQuery.field("age").greaterThan(age);
            
            return super.find(sqlQuery);
        }
    }
```

#####2.更新

```
    //类必须继承本项目中的BaseService类
    public class StudentService extends BaseService<Student,StudentMapper>{
        
        //给所有性别为male的学生加markStep分
        public int updateMarkIncBySex(int markStep,String sex) {
            SqlQuery sqlQuery = SqlQuery.newInstant();
            sqlQuery.field("sex").equal("male");
            
            SqlUpdate sqlUpdate = SqlUpdate.newInstant();
            sqlUpdate.inc("mark",markStep);
            
            return super.update(sqlQuery,sqlUpdate);
        }
    }
```

#####3.删除

```
    //类必须继承本项目中的BaseService类
    public class StudentService extends BaseService<Student,StudentMapper>{
        
        //根据名字和年龄大小删除学生信息
        public List<Student> findByNameAndAgeGreaterThan(String name,int age) {
            SqlQuery sqlQuery = SqlQuery.newInstant();
            sqlQuery.field("name").equal(name);
            sqlQuery.field("age").greaterThan(age);
            
            return super.delete(sqlQuery);
        }
    }
```

#####4.新增（BaseService中已经带有单个和批量新增方法）

```
    public int insert(T t) {
        return mapper.insert(SqlHelper.insert(t));
    }
    
    public int batchInsert(List<T> list) {
        return mapper.insert(SqlHelper.batchInsert(list));
    }
```

#####5.自定义sql

```
    //在StudentMapper中写好相关的sql语句
    @Select("select * from Student where xx = ${xx}")
    List<Student> selectByXX(@Param("xx") String xx);
    
    //以上是StudentMapper类，以下是StudentService类
    
    public List<Student> selectByXX(String xx) {
        //调用父类中的mapper的相关方法
        return mapper.selectByXX(xx);
    }
    
```
