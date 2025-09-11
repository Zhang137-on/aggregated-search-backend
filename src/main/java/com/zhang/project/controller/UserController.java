package com.zhang.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.zhang.project.common.BaseResponse;
import com.zhang.project.common.DeleteRequest;
import com.zhang.project.common.ErrorCode;
import com.zhang.project.common.ResultUtils;
import com.zhang.project.exception.BusinessException;
import com.zhang.project.model.dto.user.*;
import com.zhang.project.model.entity.User;
import com.zhang.project.model.vo.UserVO;
import com.zhang.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author zhang
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

//    private final ExecutorService executor = Executors.newCachedThreadPool();
    /**
     * 获取线程池
     */
    private final ExecutorService executor = new ThreadPoolExecutor(
            // 核心线程数 (corePoolSize)
            10,
            // 最大线程数 (maximumPoolSize)
            100,
            // 空闲线程存活时间（秒）
            60L,
            // 时间单位
            TimeUnit.SECONDS,
            // 任务队列（有界队列）
            new LinkedBlockingQueue<>(1000),
            // 线程工厂
            Executors.defaultThreadFactory(),
            // 拒绝策略
            new ThreadPoolExecutor.AbortPolicy()
    );


    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 登陆信息
     * @return User
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        JudgingLoginInfo(userLoginRequest);
        User user = userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword(),
                request);
        return ResultUtils.success(user);
    }


    /**
     * 对用户的登录信息进行验空
     * @param userLoginRequest 登录信息
     */
    public void JudgingLoginInfo(UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 流式返回接口
     */

   @GetMapping("/stream-data")
    public SseEmitter streamData() {
        SseEmitter emitter = new SseEmitter();

        // 模拟流式数据（实际可能是数据库查询、MQ 消费等）
        executor.execute(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    emitter.send("Data chunk " + i); // 发送数据块
                    Thread.sleep(1000); // 模拟延迟
                }
                emitter.complete(); // 完成流
            } catch (Exception e) {
                emitter.completeWithError(e); // 错误处理
            }
        });
       log.info("进来了!");
       log.warn("进来了!");
       log.error("进来了!");

        return emitter;
    }

    @GetMapping("/stream-data2")
    public ResponseBodyEmitter streamData2() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        executor.execute(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    // 发送数据块（可以是 JSON、文本等）
                    emitter.send("Data chunk " + i + "\n");
                    Thread.sleep(1000); // 模拟延迟
                }
                emitter.complete(); // 完成流
            } catch (Exception e) {
                emitter.completeWithError(e); // 错误处理
            }
        });

        return emitter;
    }

//    /**
//     * 流式返回
//     */
//    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/chat")
//    public Flux<String> streamChat(@RequestBody ChatRequest request) {
//        // 调用大模型 API 并返回 Flux 流
//        return callLargeModelApi(request.message())
//                .doOnNext(chunk -> log.info("发送响应片段: {}", chunk))
//                .doOnError(error -> log.error("流式处理出错", error));
//    }
//
//    // 模拟调用大模型 API，返回 Flux 流
//    private Flux<String> callLargeModelApi(String prompt) {
//        // 实际项目中需替换为真实的大模型调用逻辑
//        return Flux.just(
//                        "您好！",
//                        "我是您的AI助手。",
//                        "您的问题是：" + prompt,
//                        "我将为您提供详细解答..."
//                )// 模拟实时响应延迟
//                .delayElements(Duration.ofMillis(300));
//    }


}
